// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.services;

public class Status {

    private Boolean healthy;
    private String statusMessage;

    Status(Boolean healthy, String statusMessage) {
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
