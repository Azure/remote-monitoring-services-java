// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.models;

import java.util.Map;

public interface IActionAsaModel {
    public String getActionType();
    public void setActionType(String actionType);
    public Map<String, Object> getParameters();
    public void setParameters(Map<String, Object> parameters);
}
