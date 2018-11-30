// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.serialization.ActionDeserializer;

import java.util.Map;

/**
 * Interface for all Actions that can be added as part of a Rule.
 * New action types should implement IActionServiceModel and be added to the ActionType enum.
 * Parameters should be a case-insensitive dictionary used to pass additional
 * information required for any given action type.
 */
@JsonDeserialize(using = ActionDeserializer.class)
@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
public interface IActionServiceModel {

    ActionType getType();

    Map<String, Object> getParameters();
}

