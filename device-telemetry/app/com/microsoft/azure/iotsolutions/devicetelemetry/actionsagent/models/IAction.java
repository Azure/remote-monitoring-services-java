// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.models;

import java.util.Map;

public interface IAction {

    String getActionType();

    void setActionType(String actionType);

    Map<String, Object> getParameters();

    void setParameters(Map<String, Object> parameters);
}
