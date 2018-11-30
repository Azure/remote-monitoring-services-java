// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PackageType {
    edgeManifest("EdgeManifest"),
    deviceConfiguration("DeviceConfiguration");

    private final String value;

    PackageType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public String toString() {
        return getValue();
    }
}