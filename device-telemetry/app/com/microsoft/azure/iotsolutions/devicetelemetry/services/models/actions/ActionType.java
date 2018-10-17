// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions;

public enum ActionType {
    Email;

    // Parse ActionType from incasesitive string value
    public static ActionType from(String value) {
        for (ActionType v : values()) {
            if (v.name().equalsIgnoreCase(value)) return v;
        }
        throw new IllegalArgumentException(String.format("ActionType(%s)", value));
    }
}
