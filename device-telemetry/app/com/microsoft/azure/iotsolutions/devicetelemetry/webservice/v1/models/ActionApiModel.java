package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.EmailServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.IActionServiceModel;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public final class ActionApiModel {
    private String Type;
    private Map<String, Object> Parameters;

    public ActionApiModel(String action, Map<String, Object> parameters) {
        Type = action;
        Parameters = parameters;
    }

    public ActionApiModel(IActionServiceModel action) {
        Type = action.getType().toString();
        Parameters = action.getParameters();
    }

    public ActionApiModel() {
        Type = "";
        Parameters = new HashMap<>();
    }
    
    @JsonProperty("Type")
    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    @JsonProperty("Parameters")
    public Map<String, Object> getParameters() {
        return Parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        Parameters = parameters;
    }

    @JsonProperty("Type")
    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    @JsonProperty("Parameters")
    public Map<String, Object> getParameters() {
        return Parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        Parameters = parameters;
    }

    public IActionServiceModel toServiceModel() throws InvalidInputException {
        IActionServiceModel.Type retType;
        try {
            retType = IActionServiceModel.Type.valueOf(Type);
            switch(retType){
                case Email:
                    return new EmailServiceModel(retType, Parameters);
                default:
                    throw new InvalidInputException(String.format("The action type %s is not valid", Type));
            }
        } catch (Exception e) {
            throw new InvalidInputException(String.format("The action type %s is not valid", Type));
        }
    }
}