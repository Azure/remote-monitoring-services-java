// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AlarmStatus {
    public final String status;

    public AlarmStatus() {
        this.status = null;
    }

    public AlarmStatus(String status) {
        this.status = status;
    }

    @JsonProperty("Status")
    public String getStatus() { return this.status; }
}
