// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.webservice.v1.controllers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.microsoft.azure.iotsolutions.storageadapter.services.IStatusService;
import com.microsoft.azure.iotsolutions.storageadapter.services.models.StatusServiceModel;
import com.microsoft.azure.iotsolutions.storageadapter.webservice.runtime.IConfig;
import com.microsoft.azure.iotsolutions.storageadapter.webservice.v1.models.StatusApiModel;
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
    public StatusController(IConfig config, IStatusService statusService) {
        this.config = config;
        this.statusService = statusService;
    }

    /**
     * @return Service health details.
     */
    public Result index() throws Exception {
        StatusServiceModel statusServiceModel = this.statusService.getStatus();
        statusServiceModel.addProperty("Port", String.valueOf(config.getPort()));
        return ok(toJson(new StatusApiModel(statusServiceModel)));
    }
}
