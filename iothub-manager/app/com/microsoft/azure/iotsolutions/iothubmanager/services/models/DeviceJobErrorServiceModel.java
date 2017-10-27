// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.ExternalDependencyException;
import play.Logger;
import play.libs.Json;

import java.io.IOException;

public class DeviceJobErrorServiceModel {

    private static final Logger.ALogger log = Logger.of(DeviceJobErrorServiceModel.class);

    private String code;
    private String description;

    /**
     * A Json string is expected here.
     * for example:
     * {
     *   "code": "GatewayTimeout",
     *   "description": "Timed out waiting for the response from device."
     * }
     *
     * @param error Json string to be expected
     */
    public DeviceJobErrorServiceModel(String error) throws ExternalDependencyException {
        try {
            JsonNode node = Json.mapper().readTree(error);
            this.code = node.get("code").asText();
            this.description = node.get("description").asText();
        } catch (IOException e) {
            String message = "Error on parsing error: " + error;
            log.warn(message, e);
            throw new ExternalDependencyException(message, e);
        }
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
