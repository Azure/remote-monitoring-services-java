// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.actions;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.models.AsaAlarmApiModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.EmailAction;

import java.util.concurrent.CompletionStage;

@ImplementedBy(EmailActionExecutor.class)
public interface IActionExecutor {
    CompletionStage execute(EmailAction emailAction, AsaAlarmApiModel alarm);
}