// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SeverityType {
    CRITICAL("Critical"),
    WARNING("Warning"),
    INFO("Info");

    private String value = null;

    SeverityType(String severityStr) {
        this.value = severityStr;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public String toString() {
        return getValue();
    }
}
