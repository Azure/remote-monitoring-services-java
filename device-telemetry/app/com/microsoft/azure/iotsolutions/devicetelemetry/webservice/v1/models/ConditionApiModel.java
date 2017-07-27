// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.ConditionServiceModel;

/**
 * Public model used by the web service.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ConditionApiModel {

    private String field = null;
    private String operator = null;
    private String value = null;

    /**
     * Create an instance given the property values.
     *
     * @param field
     * @param operator
     * @param value
     */
    public ConditionApiModel(
        final String field,
        final String operator,
        final String value) {
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    /**
     * Create instance given a service model.
     *
     * @param condition service model
     */
    public ConditionApiModel(final ConditionServiceModel condition) {
        if (condition != null) {
            this.field = condition.getField();
            this.operator = condition.getOperator();
            this.value = condition.getValue();
        }
    }

    @JsonProperty("Field")
    public String getField() {
        return this.field;
    }

    @JsonProperty("Operator")
    public String getOperator() {
        return this.operator;
    }

    @JsonProperty("Value")
    public String getValue() {
        return this.value;
    }
}
