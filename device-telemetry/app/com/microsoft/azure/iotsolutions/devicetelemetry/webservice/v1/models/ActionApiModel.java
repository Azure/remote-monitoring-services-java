package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidInputException;
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

    public IActionServiceModel toServiceModel() throws InvalidInputException {
        IActionServiceModel.Type retType;
        try {
            retType = IActionServiceModel.Type.valueOf(Type); // parse to enum
            Object[] obj = {retType, Parameters}; // wrap parameters for constructor
            Class[] type = {IActionServiceModel.Type.class, java.util.Map.class}; // define type of constructor
            Class classDef = Class.forName("com.microsoft.azure.iotsolutions.devicetelemetry.services.models." + Type + "ServiceModel"); // get class definition
            Constructor cons = classDef.getConstructor(type); // get constructor for class
            return (IActionServiceModel) cons.newInstance(obj); // return IActionServiceModel reflectively
        } catch (Exception e) {
            throw new InvalidInputException(String.format("The action type %s is not valid", Type));
        }
    }
}
