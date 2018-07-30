// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.controllers;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.iothubmanager.services.IIoTHubWrapper;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models.StatusApiModel;
import com.microsoft.azure.sdk.iot.service.RegistryManager;
import play.mvc.Result;

import static play.libs.Json.toJson;
import static play.mvc.Results.ok;

/**
 * Service health check endpoint.
 */
public final class StatusController {

    private final IIoTHubWrapper ioTHubWrapper;

    @Inject
    public StatusController(IIoTHubWrapper ioTHubWrapper) {
        this.ioTHubWrapper = ioTHubWrapper;
    }

    /**
     * @return Service health status.
     */
    public Result get() {
        StatusApiModel status = new StatusApiModel(true, "Alive and well");
        try {
            RegistryManager registry = this.ioTHubWrapper.getRegistryManagerClient();
        } catch (Exception e) {
            status = new StatusApiModel(false, e.getMessage());
            status.getDependencies().put("IoTHub", "ERROR:" + e.getMessage());
        } finally {
            return ok(toJson(status));
        }
    }
}
