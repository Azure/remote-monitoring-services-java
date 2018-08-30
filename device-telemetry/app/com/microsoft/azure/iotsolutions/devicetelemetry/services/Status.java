// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services;

import com.google.inject.Inject;

public class Status {

    private String name;
    private Boolean healthy;
    private String statusMessage;

    @Inject
    public Status(final String name, final Boolean healthy, final String statusMessage) {
        this.name = name;
        this.healthy = healthy;
        this.statusMessage = statusMessage;
    }

    public String getName () {
        return this.name;
    }

    public Boolean isHealthy() {
        return this.healthy;
    }

    public String getStatusMessage() {
        return this.statusMessage;
    }
}
