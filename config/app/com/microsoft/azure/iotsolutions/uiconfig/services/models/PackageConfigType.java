package com.microsoft.azure.iotsolutions.uiconfig.services.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PackageConfigType {
    firmwareUpdateMxChip("FirmwareUpdateMxChip"),
    custom("Custom");

    private String value;

    PackageConfigType(String value) {
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
