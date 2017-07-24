// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services;

import com.google.inject.Inject;

public class StatusTuple {

    private Boolean healthy;
    private String statusMessage;

    @Inject
    public StatusTuple(Boolean healthy, String statusMessage) {
        this.healthy = healthy;
        this.statusMessage = statusMessage;
    }

    public Boolean isHealthy() {
        return this.healthy;
    }

    public String getStatusMessage() {
        return this.statusMessage;
    }
}
