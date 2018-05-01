// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OperatorType {
    GREATERTHAN("GreaterThan"),
    GREATERTHANOREQUAL("GreaterThanOrEqual"),
    LESSTHAN("LessThan"),
    LESSTHANOREQUAL("LessThanOrEqual"),
    EQUALS("Equals");

    private String value = null;

    OperatorType(String operatorStr) {
        this.value = operatorStr;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public String toString() {
        return getValue();
    }
}
