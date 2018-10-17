// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.actions;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.models.AsaAlarmApiModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.EmailActionServiceModel;

import java.util.concurrent.CompletionStage;

@ImplementedBy(EmailActionExecutor.class)
public interface IActionExecutor {
    CompletionStage execute(EmailActionServiceModel emailAction, AsaAlarmApiModel alarm);
}
