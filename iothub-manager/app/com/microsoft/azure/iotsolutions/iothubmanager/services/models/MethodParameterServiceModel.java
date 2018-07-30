// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.ExternalDependencyException;
import play.Logger;
import play.libs.Json;

import java.io.IOException;
import java.time.Duration;

public class MethodParameterServiceModel {

    private static final Logger.ALogger log = Logger.of(MethodParameterServiceModel.class);


    private String name = null;
    private Duration responseTimeout = null;
    private Duration connectionTimeout = null;
    private String jsonPayload = null;

    public MethodParameterServiceModel() {

    }

    /**
     * A Json string is expected here.
     * for example:
     *  {
     *      "methodName": "Reboot",
     *      "responseTimeoutInSeconds": 60,
     *      "connectTimeoutInSeconds": 60,
     *      "payload": "{\"foo\": \"bar\"}"
     *  }
     * @param cloudToDeviceMethod Json string to be expected
     */
    public MethodParameterServiceModel(String cloudToDeviceMethod) throws ExternalDependencyException {
        try {
            JsonNode node = Json.mapper().readTree(cloudToDeviceMethod);
            this.name = node.get("methodName").asText();
            this.responseTimeout = Duration.ofSeconds(node.get("responseTimeoutInSeconds").asLong());
            this.connectionTimeout = Duration.ofSeconds(node.get("connectTimeoutInSeconds").asLong());
            this.jsonPayload = node.get("payload").asText();
        } catch (IOException e) {
            String message = "Error on parsing cloudToDeviceMethod: " + cloudToDeviceMethod;
            log.warn(message, e);
            throw new ExternalDependencyException(message, e);
        }
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
