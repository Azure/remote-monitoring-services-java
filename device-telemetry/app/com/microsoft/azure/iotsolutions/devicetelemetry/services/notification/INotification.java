// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.notification;

import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.models.AlarmNotificationAsaModel;

import java.util.concurrent.CompletionStage;

public interface INotification {

    public enum EmailImplementationTypes {
        LogicApp
    }

    public AlarmNotificationAsaModel getAlarm();

    public void setAlarm(AlarmNotificationAsaModel model);

    public CompletionStage executeAsync();
}
