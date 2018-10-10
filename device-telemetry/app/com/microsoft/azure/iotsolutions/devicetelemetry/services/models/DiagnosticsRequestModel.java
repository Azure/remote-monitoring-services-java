// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Dictionary;

public class DiagnosticsRequestModel {
    private String eventType;
    private Dictionary<String, Object> eventProperties;

    public DiagnosticsRequestModel(String eventType, Dictionary<String, Object> eventProperties) {
        this.eventType = eventType;
        this.eventProperties = eventProperties;
    }

    @JsonProperty("EventType")
    public String getEventType() { return this.eventType; }

    public void setEventType(String eventType) { this.eventType = eventType; }

    @JsonProperty("EventProperties")
    public Dictionary<String, Object> getEventProperties() {
        return this.eventProperties;
    }

    public void setEventProperties(Dictionary<String, Object> eventProperties) {
        this.eventProperties = eventProperties;
    }
}
