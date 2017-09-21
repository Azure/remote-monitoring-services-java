// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

public class DeviceRegistryApiModel {

    private HashMap<String, Object> tags;
    private DeviceTwinProperties properties;

    @JsonProperty("Tags")
    public HashMap<String, Object> getTags() {
        return this.tags;
    }

    public void setTags(HashMap<String, Object> value) {
        this.tags = value;
    }

    @JsonProperty("Properties")
    public DeviceTwinProperties getProperties() {
        return this.properties;
    }

    public void setProperties(DeviceTwinProperties value) {
        this.properties = value;
    }
}
