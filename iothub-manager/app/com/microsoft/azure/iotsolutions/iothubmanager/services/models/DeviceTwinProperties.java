// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import play.api.Mode;

import java.util.HashMap;

public class DeviceTwinProperties {

    private HashMap<String, Object> desired;
    private HashMap<String, Object> reported;

    public DeviceTwinProperties(HashMap<String, Object> desired, HashMap<String, Object> reported) {
        this.desired = desired;
        this.reported = reported;
    }

    public DeviceTwinProperties() {}

    @JsonProperty("Desired")
    public HashMap<String, Object> getDesired() {
        return desired;
    }

    public void setDesired(HashMap<String, Object> desired) {
        this.desired = desired;
    }

    @JsonProperty("Reported")
    public HashMap<String, Object> getReported() {
        return reported;
    }

    public void setReported(HashMap<String, Object> reported) {
        this.reported = reported;
    }
}
