// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.actions;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.models.AsaAlarmApiModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.ResourceNotFoundException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.ActionType;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.EmailActionServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.IActionServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServicesConfig;
import play.libs.ws.WSClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class ActionManager implements IActionManager {

    private IActionExecutor actionExecutor;

    @Inject
    public ActionManager(IServicesConfig servicesConfig, WSClient wsClient) throws ResourceNotFoundException {
        this.actionExecutor = new EmailActionExecutor(servicesConfig, wsClient);
    }

    public CompletionStage executeAsync(List<AsaAlarmApiModel> alarms) {
        List<CompletionStage> tasks = new ArrayList<>();

        alarms.stream()
            .filter(alarm -> alarm.getActions() != null)
            .collect(Collectors.toList())
            .forEach(alarm -> {
                alarm.getActions().stream()
                    .filter(action -> action.getType().equals(ActionType.Email))
                    .forEach(action -> tasks.add(actionExecutor.execute((EmailActionServiceModel) action, alarm)));
            });

        CompletableFuture[] futures = tasks.stream()
            .map(t -> t.toCompletableFuture())
            .collect(Collectors.toList())
            .toArray(new CompletableFuture[tasks.size()]);

        return CompletableFuture.allOf(futures);
    }
}
