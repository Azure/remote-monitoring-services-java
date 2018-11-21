// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.services.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StatusResultServiceModel {
    private boolean isHealthy;
    private String message;

    @JsonCreator
    public StatusResultServiceModel(@JsonProperty("IsHealthy") boolean isHealthy, @JsonProperty("Message") String message) {
        this.isHealthy = isHealthy;
        this.message = message;
    }

    public void setStatusResultServiceModel(StatusResultServiceModel model) {
        this.isHealthy = model.isHealthy;
        this.message = model.message;
    }

    public boolean getIsHealthy() {
        return this.isHealthy;
    }

    public String getMessage() {
        return this.message;
    }

    @JsonProperty("IsHealthy")
    public void setIsHealthy(boolean isHealthy) {
        this.isHealthy = isHealthy;
    }

    @JsonProperty("Message")
    public void setMessage(String message) {
        this.message = message;
    }
}
