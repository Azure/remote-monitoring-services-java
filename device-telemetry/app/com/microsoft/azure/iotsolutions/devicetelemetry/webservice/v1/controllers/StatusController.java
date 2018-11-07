// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.controllers;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.IStatusService;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.StatusServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.runtime.IConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.StatusApiModel;
import play.mvc.Controller;
import play.mvc.Result;

import static play.libs.Json.toJson;

/**
 * Service health check endpoint.
 */
public final class StatusController extends Controller {

    private final IConfig config;
    private final IStatusService statusService;

    @Inject
    public StatusController(IConfig config, IStatusService statusService) {
        this.config = config;
        this.statusService = statusService;
    }

    /**
     * @return Service health details.
     */
    public Result index() throws Exception {
        StatusServiceModel statusServiceModel = this.statusService.GetStatusAsync(
                config.getClientAuthConfig().isAuthRequired())
                .toCompletableFuture().get();
        statusServiceModel.addProperty("Port", String.valueOf(config.getPort()));
        statusServiceModel.addProperty("AuthRequired", String.valueOf(config.getClientAuthConfig().isAuthRequired()));
        return ok(toJson(new StatusApiModel(statusServiceModel)));
    }
}
