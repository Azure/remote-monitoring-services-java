// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.actions;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.models.AsaAlarmsApiModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.ResourceNotFoundException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.EmailAction;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.IAction;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServiceConfig;
import play.libs.ws.WSClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class ActionManager implements IActionManager {

    private IActionExecutor actionExecutor;

    @Inject
    public ActionManager(IServiceConfig servicesConfig, WSClient wsClient) throws ResourceNotFoundException {
        this.actionExecutor = new EmailActionExecutor(servicesConfig, wsClient);
    }

    public CompletionStage executeAsync(List<AsaAlarmsApiModel> alarms) {
        List<CompletionStage> tasks = new ArrayList<>();
        for (AsaAlarmsApiModel alarm : alarms) {
            for (IAction action : alarm.getActions()) {
                switch (action.getType()) {
                    case Email:
                        tasks.add(actionExecutor.execute((EmailAction) action, alarm));
                        break;
                }
            }
        }

        List<CompletableFuture> results = tasks.stream()
            .map(t -> t.toCompletableFuture())
            .collect(Collectors.toList());

        return CompletableFuture.allOf((CompletableFuture<?>) results);
    }
}
