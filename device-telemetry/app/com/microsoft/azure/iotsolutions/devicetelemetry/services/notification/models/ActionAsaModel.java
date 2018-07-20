// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class ActionAsaModel implements IActionAsaModel{
    @JsonProperty("Type")
    private String ActionType = "";

    @JsonProperty("Parameters")
    private Map<String, Object> Parameters = new HashMap<>();

    public ActionAsaModel() {
        // empty constructor
    }

    @JsonProperty("Type")
    @Override
    public String getActionType() {
        return ActionType;
    }

    @Override
    public void setActionType(String actionType) {
        ActionType = actionType;
    }

    @JsonProperty("Parameters")
    @Override
    public Map<String, Object> getParameters() {
        return Parameters;
    }

    @Override
    public void setParameters(Map<String, Object> parameters) {
        Parameters = parameters;
    }
}
