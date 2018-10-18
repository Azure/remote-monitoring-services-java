// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.controllers;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.Status;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidConfigurationException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.MessagesConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.StorageType;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.storage.cosmosDb.IStorageClient;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.storage.storageAdapter.IKeyValueClient;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.storage.timeSeries.ITimeSeriesClient;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.runtime.IConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.StatusApiModel;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.concurrent.CompletionStage;

import static play.libs.Json.toJson;

/**
 * Service health check endpoint.
 */
public final class StatusController extends Controller {

    private final IConfig config;
    private final IStorageClient storageClient;
    private final IKeyValueClient keyValueClient;
    private final ITimeSeriesClient timeSeriesClient;
    private final String STORAGE_TYPE_KEY = "StorageType";
    private final String TSI_EXPLORER_URL = "TsiExplorerUrl";

    @Inject
    public StatusController(
        IConfig config,
        IStorageClient storageClient,
        IKeyValueClient keyValueClient,
        ITimeSeriesClient timeSeriesClient) {
        this.config = config;
        this.storageClient = storageClient;
        this.keyValueClient = keyValueClient;
        this.timeSeriesClient = timeSeriesClient;
    }

    /**
     * @return Service health details.
     */
    public CompletionStage<Result> index() throws InvalidConfigurationException {
        StatusApiModel statusApiModel = new StatusApiModel();

        Status storageClientStatus = this.storageClient.ping();
        statusApiModel.addStatus(storageClientStatus);

        MessagesConfig messagesConfig = this.config.getServicesConfig().getMessagesConfig();
        if (messagesConfig.getStorageType() == StorageType.tsi) {
            Status timeSeriesClientStatus = this.timeSeriesClient.ping();
            statusApiModel.addStatus(timeSeriesClientStatus);
            statusApiModel.getProperties().put(TSI_EXPLORER_URL,
                messagesConfig.getTimeSeriesConfig().getExplorerUrl());
        }

        CompletionStage<Status> keyValueStorageStatusResult = this.keyValueClient.pingAsync();

        return keyValueStorageStatusResult
            .thenApply(keyValueStatus -> {
                statusApiModel.addStatus(keyValueStatus);
                statusApiModel.getProperties()
                    .put(STORAGE_TYPE_KEY, messagesConfig.getStorageType().toString());
                return ok(toJson(statusApiModel));
            });
    }
}
