// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions;

import java.util.Map;

/// <summary>
/// Interface for all Actions that can be added as part of a Rule.
/// New action types should implement IAction and be added to the ActionType enum.
/// Parameters should be a case-insensitive dictionary used to pass additional
/// information required for any given action type.
/// </summary>
public interface IActionServiceModel {
    public ActionType getType();

    public Map<String, Object> getParameters();
}

enum ActionType {
    Email
}
