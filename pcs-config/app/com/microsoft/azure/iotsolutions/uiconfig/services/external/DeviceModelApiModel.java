// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Hashtable;

public class DeviceModelApiModel {

    private Hashtable<String, Object> properties;

    @JsonProperty("Properties")
    public Hashtable<String, Object> getProperties() {
        return this.properties;
    }

    public void setProperties(Hashtable<String, Object> properties) {
        this.properties = properties;
    }
}
