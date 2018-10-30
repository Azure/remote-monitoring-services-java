// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.actions;

import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.models.AsaAlarmApiModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.IActionServiceModel;

import java.util.concurrent.CompletionStage;

public interface IActionExecutor {
    CompletionStage execute(IActionServiceModel action, AsaAlarmApiModel alarm);
}
