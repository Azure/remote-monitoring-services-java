// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.actions;

import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.models.AsaAlarmApiModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.IActionServiceModel;

import java.util.concurrent.CompletionStage;

/**
 * Executing an action for one alarm.
 */
public interface IActionExecutor {
    /**
     *
     * @param action the action to be executed for the alarm
     * @param alarm the alarm to include the information to trigger action
     * @return the new CompletionStage
     */
    CompletionStage execute(IActionServiceModel action, AsaAlarmApiModel alarm);
}
