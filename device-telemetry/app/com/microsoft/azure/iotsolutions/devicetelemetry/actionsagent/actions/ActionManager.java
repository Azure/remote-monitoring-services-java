// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.actions;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.models.AsaAlarmApiModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.ResourceNotFoundException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.ActionType;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServicesConfig;
import play.libs.ws.WSClient;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class ActionManager implements IActionManager {

    private HashMap<ActionType, IActionExecutor> actionExecutors;

    @Inject
    public ActionManager(IServicesConfig servicesConfig, WSClient wsClient) throws ResourceNotFoundException {
        this.actionExecutors = new HashMap<ActionType, IActionExecutor>()
        {{
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
                        IActionExecutor executor = this.actionExecutors.get(action.getType());
                        if (executor != null) {
                            tasks.add(executor.execute(action, alarm));
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
