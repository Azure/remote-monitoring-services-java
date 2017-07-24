// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.controllers;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.IStorageClient;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.StatusTuple;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.StatusApiModel;
import play.mvc.Controller;
import play.mvc.Result;

import static play.libs.Json.toJson;

/**
 * Service health check endpoint.
 */
public final class StatusController extends Controller {

    private final IStorageClient storageClient;

    @Inject
    public StatusController(IStorageClient storageClient) {
        this.storageClient = storageClient;
    }

    /**
     * @return Service health details.
     */
    public Result index() {
        StatusTuple storageClientStatusTuple = this.storageClient.Ping();

        return ok(toJson(new StatusApiModel(storageClientStatusTuple)));
    }
}
