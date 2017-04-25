// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.template.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Building {

    final List<Device> devices;

    @JsonCreator
    Building(@JsonProperty("devices") List<Device> devices) {
        this.devices = devices;
    }

    public List<Device> getDevices() {
        return devices;
    }
}
