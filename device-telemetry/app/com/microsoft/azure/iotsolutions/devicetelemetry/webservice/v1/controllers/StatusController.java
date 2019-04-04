// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.controllers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.IStatusService;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.StatusServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.auth.Authorize;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.runtime.IConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.StatusApiModel;
import play.mvc.Controller;
import play.mvc.Result;

import static play.libs.Json.toJson;

/**
 * Service health check endpoint.
 */
@Singleton
public final class StatusController extends Controller {

    private final IConfig config;
    private final IStatusService statusService;

    @Inject
    public StatusController(IStatusService statusService, IConfig config) {
        this.config = config;
        this.statusService = statusService;
    }

    /**
     * @return Service health details.
     */
     @Authorize("ReadAll")
    public Result index() throws Exception {
        StatusServiceModel statusServiceModel = this.statusService.getStatus(config.getClientAuthConfig().isAuthRequired());
        statusServiceModel.addProperty("Port", String.valueOf(config.getPort()));
        statusServiceModel.addProperty("AuthRequired", String.valueOf(config.getClientAuthConfig().isAuthRequired()));
        return ok(toJson(new StatusApiModel(statusServiceModel)));
    }
}
