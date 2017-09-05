// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.models;

import java.time.Duration;

public class MethodParameterServiceModel {

    private String name = null;
    private Duration responseTimeout = null;
    private Duration connectionTimeout = null;
    private String jsonPayload = null;

    public MethodParameterServiceModel() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Duration getResponseTimeout() {
        return responseTimeout;
    }

    public void setResponseTimeout(Duration responseTimeout) {
        this.responseTimeout = responseTimeout;
    }

    public Duration getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Duration connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public String getJsonPayload() {
        return jsonPayload;
    }

    public void setJsonPayload(String jsonPayload) {
        this.jsonPayload = jsonPayload;
    }
}
