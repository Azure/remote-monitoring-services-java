// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.webservice.v1.controllers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.microsoft.azure.iotsolutions.storageadapter.services.DocDBKeyValueContainer;
import com.microsoft.azure.iotsolutions.storageadapter.services.IKeyValueContainer;
import com.microsoft.azure.iotsolutions.storageadapter.services.Status;
import com.microsoft.azure.iotsolutions.storageadapter.webservice.v1.models.StatusApiModel;
import play.mvc.Result;

import static play.libs.Json.toJson;
import static play.mvc.Results.ok;


/**
 * Service health check endpoint.
 */
@Singleton
public final class StatusController {

    private final IKeyValueContainer storageClient;

    @Inject
    public StatusController(DocDBKeyValueContainer storageClient) {
        this.storageClient = storageClient;
    }

    /**
     * @return Service health details.
     */
    public Result index() {
        Status status = storageClient.ping();
        return ok(toJson(new StatusApiModel(status)));
    }
}
