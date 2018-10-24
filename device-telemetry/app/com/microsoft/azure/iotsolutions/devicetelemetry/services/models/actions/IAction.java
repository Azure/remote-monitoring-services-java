// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Map;

@JsonSerialize(as = IAction.class)
@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
public interface IAction {

    enum ActionType {
        Email
    }

    ActionType getType();

    void setType(ActionType Type);

    Map<String, Object> getParameters();

    void setParameters(Map<String, Object> parameters);
}
