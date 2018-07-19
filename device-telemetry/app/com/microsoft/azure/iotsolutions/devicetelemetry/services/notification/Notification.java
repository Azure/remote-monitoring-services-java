package com.microsoft.azure.iotsolutions.devicetelemetry.services.notification;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.implementation.IImplementation;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.models.ActionAsaModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.models.AlarmNotificationAsaModel;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;

public class Notification implements INotification {
    private INotification.EmailImplementationTypes EMAIL_IMPLEMENTATION_TYPE = INotification.EmailImplementationTypes.LogicApp;
    private IImplementationWrapper implementationWrapper;
    private IImplementation implementation;
    private AlarmNotificationAsaModel alarm;

    @Inject
    public Notification(IImplementationWrapper implementationWrapper) {
        this.implementationWrapper = implementationWrapper;
    }

    @Override
    public AlarmNotificationAsaModel getAlarm() {
        return this.alarm;
    }

    @Override
    public void setAlarm(AlarmNotificationAsaModel model){
        this.alarm = model;
    }

    public CompletionStage executeAsync() {
        try {
            for(ActionAsaModel action : this.alarm.getActions()){
                switch (action.getActionType()){
                    case "Email":
                        implementation = this.implementationWrapper.getImplementationType(EMAIL_IMPLEMENTATION_TYPE);
                }
                implementation.setMessage((String) action.getParameters().get("Template"), this.alarm.getRule_id(), this.alarm.getRule_description());
                if(action.getParameters().get("Email") != null) {
                    implementation.setReceiver(((ArrayList<String>) action.getParameters().get("Email")));
                }
                implementation.execute();
            }
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            throw new CompletionException(e);
        }
    }
}
