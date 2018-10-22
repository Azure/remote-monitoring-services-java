// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class ActionAsaModel implements IAction {
    @JsonProperty("ActionType")
    private String actionType = "";

    @JsonProperty("Parameters")
    private Map<String, Object> parameters = new HashMap<>();

    @JsonProperty("ActionType")
    @Override
    public String getActionType() {
        return this.actionType;
    }

    @Override
    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    @JsonProperty("Parameters")
    @Override
    public Map<String, Object> getParameters() {
        return this.parameters;
    }

    @Override
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}
