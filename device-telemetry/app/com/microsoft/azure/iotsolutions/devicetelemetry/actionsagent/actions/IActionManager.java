// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.actions;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.models.AsaAlarmApiModel;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Manage the execution of actions for a list of alarms with
 * different action types.
 */
@ImplementedBy(ActionManager.class)
public interface IActionManager {
    /**
     *
     * @param alarms The list of alarms to be executed at a time
     * @return the new CompletionStage
     */
    CompletionStage executeAsync(List<AsaAlarmApiModel> alarms);
}
