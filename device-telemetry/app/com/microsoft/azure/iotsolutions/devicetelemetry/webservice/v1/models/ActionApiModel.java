package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models;

import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.IActionServiceModel;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public final class ActionApiModel {
    public String Type = "";
    public Map<String, Object> Parameters = new HashMap<>();

    public ActionApiModel(String action, Map<String, Object> parameters) {
        Type = action;
        Parameters = parameters;
    }

    public ActionApiModel(IActionServiceModel action) {
        Type = action.getType().toString();
        Parameters = action.getParameters();
    }

    public ActionApiModel() {
        // empty constructor
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
