// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.models;

/*
 * Specifies a condition that must be met for a rule to trigger an alarm.
 */
public final class ConditionServiceModel {

    private final String field;

    private final String operator;
    private final String value;

    public ConditionServiceModel(
        final String field,
        final String operator,
        final String value) {

        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    public String getField() {
        return this.field;
    }

    public String getOperator() {
        return this.operator;
    }

    public String getValue() {
        return this.value;
    }
}
