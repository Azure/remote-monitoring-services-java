// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CalculationType {
    AVERAGE("Average"),
    INSTANT("Instant");

    private String value = null;

    CalculationType(String calculationStr) {
        this.value = calculationStr;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public String toString() {
        return getValue();
    }
}
