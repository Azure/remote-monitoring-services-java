// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.actions.IActionExecutor;
import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.models.ActionAsaModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.models.AsaAlarmsApiModel;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;

public class Notification implements INotification {
    private INotification.EmailImplementationTypes EMAIL_IMPLEMENTATION_TYPE = INotification.EmailImplementationTypes.LogicApp;
    private INotificationImplementationWrapper implementationWrapper;
    private IActionExecutor implementation;
    private AsaAlarmsApiModel alarm;
    private static final String EMAIL = "Email";
    private static final String TEMPLATE = "Template";

    @Inject
    public Notification(INotificationImplementationWrapper implementationWrapper) {
        this.implementationWrapper = implementationWrapper;
    }

    @Override
    public AsaAlarmsApiModel getAlarm() {
        return this.alarm;
    }

    @Override
    public void setAlarm(AsaAlarmsApiModel model) {
        this.alarm = model;
    }

    public CompletionStage executeAsync() {
        try {
            for(ActionAsaModel action : this.alarm.getActions()) {
                switch (action.getActionType()){
                    case EMAIL:
                        implementation = this.implementationWrapper.getImplementationType(EMAIL_IMPLEMENTATION_TYPE);
                }
                implementation.setMessage((String) action.getParameters().get(TEMPLATE), this.alarm.getRuleId(), this.alarm.getRuleDescription());
                if(action.getParameters().get(EMAIL) != null) {
                    implementation.setReceiver(((ArrayList<String>) action.getParameters().get(EMAIL)));
                }
                implementation.execute();
            }
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            throw new CompletionException(e);
        }
    }
}
