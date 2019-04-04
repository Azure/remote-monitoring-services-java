// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.controllers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.microsoft.azure.iotsolutions.uiconfig.services.IStatusService;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.InvalidConfigurationException;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.StatusServiceModel;
import com.microsoft.azure.iotsolutions.uiconfig.webservice.auth.Authorize;
import com.microsoft.azure.iotsolutions.uiconfig.webservice.runtime.IConfig;
import com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.models.StatusApiModel;
import play.mvc.Result;

import static play.libs.Json.toJson;
import static play.mvc.Results.ok;

/**
 * Service health check endpoint.
 */
@Singleton
public final class StatusController {
    private final IConfig config;
    private final IStatusService statusService;

    @Inject
    public StatusController(IStatusService statusService, IConfig config) {
        this.config = config;
        this.statusService = statusService;
    }

    /**
     * @return Service health status.
     */
    @Authorize("ReadAll")
    public Result index() throws InvalidConfigurationException {
        StatusServiceModel statusServiceModel = this.statusService.getStatus();
        statusServiceModel.addProperty("Port", String.valueOf(config.getPort()));
        statusServiceModel.addProperty("AuthRequired", String.valueOf(config.getClientAuthConfig().isAuthRequired()));
        return ok(toJson(new StatusApiModel(statusServiceModel)));
    }
}
