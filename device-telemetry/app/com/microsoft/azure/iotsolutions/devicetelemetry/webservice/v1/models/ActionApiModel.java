package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.EmailAction;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.IAction;

import java.util.HashMap;
import java.util.Map;

public final class ActionApiModel {
    private String type;
    private Map<String, Object> parameters;

    public ActionApiModel(String action, Map<String, Object> parameters) {
        this.type = action;
        this.parameters = parameters;
    }

    public ActionApiModel(IAction action) {
        this.type = action.getType().toString();
        this.parameters = action.getParameters();
    }

    public ActionApiModel() {
        this.type = "";
        this.parameters = new HashMap<>();
    }

    @JsonProperty("ActionType")
    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("Parameters")
    public Map<String, Object> getParameters() {
        return this.parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public IAction toServiceModel() throws InvalidInputException {
        IAction.ActionType retType;
        try {
            retType = IAction.ActionType.valueOf(this.type);
            switch(retType){
                case Email:
                    return new EmailAction(retType, this.parameters);
                default:
                    throw new InvalidInputException(String.format("The action type %s is not valid", this.type));
            }
        } catch (Exception e) {
            throw new InvalidInputException(String.format("The action type %s is not valid", this.type));
        }
    }
}