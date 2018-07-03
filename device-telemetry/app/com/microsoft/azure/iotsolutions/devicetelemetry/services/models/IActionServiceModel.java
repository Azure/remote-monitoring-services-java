package com.microsoft.azure.iotsolutions.devicetelemetry.services.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Map;

@JsonSerialize(as=IActionServiceModel.class)
public interface IActionServiceModel {
    enum Type{
        Email
    }

    Type getActionType();

    void setActionType(Type actionType);

    Map<String, Object> getParameters();
}
