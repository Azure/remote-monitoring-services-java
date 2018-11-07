package com.microsoft.azure.iotsolutions.uiconfig.services.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PackageConfigType {
    firmwareUpdateMxChip("FirmwareUpdateMxChip"),
    custom("Custom");

    private final String value;

    PackageConfigType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getValue();
    }
}
