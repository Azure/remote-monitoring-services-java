// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.controllers;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.IKeyValueClient;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.IStorageClient;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.Status;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.StatusApiModel;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.concurrent.CompletionStage;

import static play.libs.Json.toJson;

/**
 * Service health check endpoint.
 */
public final class StatusController extends Controller {

    private final IStorageClient storageClient;
    private final IKeyValueClient keyValueClient;

    @Inject
    public StatusController(
        IStorageClient storageClient,
        IKeyValueClient keyValueClient) {
        this.storageClient = storageClient;
        this.keyValueClient = keyValueClient;
    }

    /**
     * @return Service health details.
     */
    public CompletionStage<Result> index() {
        Status storageClientStatus = this.storageClient.Ping();
        CompletionStage<Status> keyValueStorageStatusResult = this.keyValueClient.pingAsync();

        return keyValueStorageStatusResult
            .thenApply(keyValueStatus -> {
                if (keyValueStatus.isHealthy()) {
                    return ok(toJson(
                        new StatusApiModel(storageClientStatus, keyValueStatus)));
                }
                else {
                    return ok(toJson(
                        new StatusApiModel(storageClientStatus, keyValueStatus)));
                }
            });
    }
}
