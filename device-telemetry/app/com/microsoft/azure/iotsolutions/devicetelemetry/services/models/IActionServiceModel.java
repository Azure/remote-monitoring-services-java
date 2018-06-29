package com.microsoft.azure.iotsolutions.devicetelemetry.services.models;

import java.util.Map;

public interface IActionServiceModel {
    enum Type{
        Email
    }

    Type getActionType();

    void setActionType(Type actionType);

    Map<String, Object> getParameters();
}
