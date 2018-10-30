// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.actions;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.models.AsaAlarmApiModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.ResourceNotFoundException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.ActionType;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServicesConfig;
import play.Logger;
import play.libs.ws.WSClient;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class ActionManager implements IActionManager {

    private HashMap<ActionType, IActionExecutor> actionExecutorsMap;
    private static final Logger.ALogger log = Logger.of(ActionManager.class);

    @Inject
    public ActionManager(IServicesConfig servicesConfig, WSClient wsClient) throws ResourceNotFoundException {
        this.actionExecutorsMap = new HashMap<ActionType, IActionExecutor>() {{
            put(ActionType.Email, new EmailActionExecutor(servicesConfig, wsClient));
        }};
    }

    public CompletionStage executeAsync(List<AsaAlarmApiModel> alarms) {
        List<CompletionStage> tasks = new ArrayList<>();

        alarms.stream()
            .filter(alarm -> alarm.getActions() != null)
            .collect(Collectors.toList())
            .forEach(alarm -> {
                alarm.getActions().stream()
                    .forEach(action -> {
                        IActionExecutor executor = this.actionExecutorsMap.get(action.getType());
                        if (executor != null) {
                            tasks.add(executor.execute(action, alarm));
                        } else {
                            this.log.error("No IActionExecutor implementation for this action type: "
                                + action.getType().name());
                        }
                    });
            });

        CompletableFuture[] futures = tasks.stream()
            .map(t -> t.toCompletableFuture())
            .collect(Collectors.toList())
            .toArray(new CompletableFuture[tasks.size()]);

        return CompletableFuture.allOf(futures);
    }
}
