// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.actions;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.models.AsaAlarmApiModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.ResourceNotFoundException;
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
        for (AsaAlarmApiModel alarm : alarms) {
            if (alarm.getActions() != null) {
                for (IActionServiceModel action : alarm.getActions()) {
                    switch (action.getType()) {
                        case Email:
                            tasks.add(actionExecutor.execute((EmailActionServiceModel) action, alarm));
                            break;
                    }
                }
            }
        }

        CompletableFuture[] futures = tasks.stream()
            .map(t -> t.toCompletableFuture())
            .collect(Collectors.toList())
            .toArray(new CompletableFuture[tasks.size()]);

        return CompletableFuture.allOf(futures);
    }
}
