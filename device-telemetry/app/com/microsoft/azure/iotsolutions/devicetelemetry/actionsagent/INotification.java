// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent;

import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.models.AsaAlarmsApiModel;

import java.util.concurrent.CompletionStage;

public interface INotification {

    enum EmailImplementationTypes {
        LogicApp
    }

    AsaAlarmsApiModel getAlarm();

    void setAlarm(AsaAlarmsApiModel model);

    CompletionStage executeAsync();
}
