// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.serialization.ActionConverter;

import java.util.Map;

@JsonSerialize(as = IAction.class)
@JsonDeserialize(using = ActionConverter.class)
@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
public interface IAction {

    ActionType getType();

    void setType(ActionType Type);

    Map<String, Object> getParameters();

    void setParameters(Map<String, Object> parameters);
}

