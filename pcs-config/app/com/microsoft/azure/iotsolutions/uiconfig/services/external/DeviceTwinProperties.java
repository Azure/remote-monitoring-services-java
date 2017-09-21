// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

public class DeviceTwinProperties {

    private HashMap<String, Object> reported;

    public DeviceTwinProperties(HashMap<String, Object> reported) {
        this.reported = reported;
    }

    public DeviceTwinProperties() {
    }

    @JsonProperty("Reported")
    public HashMap<String, Object> getReported() {
        return reported;
    }

    @JsonProperty("Reported")
    public void setReported(HashMap<String, Object> reported) {
        this.reported = reported;
    }
}
