// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.StatusResultServiceModel;

@JsonPropertyOrder({"IsHealthy", "Message"})
public class StatusResultApiModel {
    private boolean isHealthy;
    private String message;

    public StatusResultApiModel(StatusResultServiceModel serviceModel) {
        this.isHealthy = serviceModel.getIsHealthy();
        this.message = serviceModel.getMessage();
    }

    @JsonProperty("IsHealthy")
    public boolean getIsHealthy() {
        return this.isHealthy;
    }

    @JsonProperty("Message")
    public String getMessage() {
        return this.message;
    }
}
