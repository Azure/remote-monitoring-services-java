// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.ConditionServiceModel;

/**
 * Public model used by the web service.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
public final class ConditionApiModel {

    private String field = null;
    private String operator = null;
    private String value = null;

    public ConditionApiModel() {
    }

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

    public String getField() {
        return this.field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getOperator() {
        return this.operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ConditionServiceModel toServiceModel() {
        return new ConditionServiceModel(
            this.getField(),
            this.getOperator(),
            this.getValue()
        );
    }
}
