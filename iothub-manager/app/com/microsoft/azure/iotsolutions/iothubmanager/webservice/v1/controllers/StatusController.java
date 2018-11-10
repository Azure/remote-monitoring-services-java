// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.controllers;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.iothubmanager.services.IStatusService;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.StatusServiceModel;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.runtime.IConfig;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models.StatusApiModel;
import play.mvc.Result;

import static play.libs.Json.toJson;
import static play.mvc.Results.ok;

/**
 * Service health check endpoint.
 */
public final class StatusController {

    private final IConfig config;
    private final IStatusService statusService;

    @Inject
    public StatusController(IStatusService statusService, IConfig config) {
        this.statusService = statusService;
        this.config = config;
    }

    /**
     * @return Service health status.
     */
    public Result get() {
        StatusServiceModel statusServiceModel = this.statusService.getStatus(config.getClientAuthConfig().isAuthRequired());
        statusServiceModel.addProperty("Port", String.valueOf(config.getPort()));
        statusServiceModel.addProperty("AuthRequired", String.valueOf(config.getClientAuthConfig().isAuthRequired()));
        return ok(toJson(new StatusApiModel(statusServiceModel)));
    }
}
