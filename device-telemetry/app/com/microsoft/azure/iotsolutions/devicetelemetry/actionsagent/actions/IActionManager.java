// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.actions;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.models.AsaAlarmApiModel;

import java.util.List;
import java.util.concurrent.CompletionStage;

@ImplementedBy(ActionManager.class)
public interface IActionManager {
    CompletionStage executeAsync(List<AsaAlarmApiModel> alarms);
}
