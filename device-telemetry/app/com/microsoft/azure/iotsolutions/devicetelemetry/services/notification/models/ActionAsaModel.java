package com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class ActionAsaModel {
    @JsonProperty("Type")
    private String ActionType = "";

    @JsonProperty("Parameters")
    private Map<String, Object> Parameters = new HashMap<>();

    public ActionAsaModel() {
        // empty constructor
    }

    @JsonProperty("Type")
    public String getActionType() {
        return ActionType;
    }

    public void setActionType(String actionType) {
        ActionType = actionType;
    }

    @JsonProperty("Parameters")
    public Map<String, Object> getParameters() {
        return Parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        Parameters = parameters;
    }
}
