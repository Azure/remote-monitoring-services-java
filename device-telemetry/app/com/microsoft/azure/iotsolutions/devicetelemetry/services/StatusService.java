// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.helpers.WsRequestBuilder;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.StatusResultServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.StatusServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.StorageType;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.storage.cosmosDb.IStorageClient;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.storage.timeSeries.ITimeSeriesClient;
import play.Logger;
import play.libs.ws.WSResponse;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

public class StatusService implements IStatusService {

    private final String storageAdapterName = "StorageAdapter";
    private final String diagnosticsName = "Diagnostics";
    private final String authName = "Auth";

    private final boolean ALLOW_INSECURE_SSL_SERVER = true;
    private final int timeoutMS = 10000;

    private final IStorageClient storageClient;
    private final ITimeSeriesClient timeSeriesClient;
    private final WsRequestBuilder wsRequestBuilder;
    private final IServicesConfig servicesConfig;

    private static final Logger.ALogger log = Logger.of(StatusService.class);

    @Inject
    StatusService(
            IStorageClient storageClient,
            ITimeSeriesClient timeSeriesClient,
            IServicesConfig servicesConfig,
            WsRequestBuilder wsRequestBuilder
    ) {
        this.storageClient = storageClient;
        this.wsRequestBuilder = wsRequestBuilder;
        this.timeSeriesClient = timeSeriesClient;
        this.servicesConfig = servicesConfig;
    }

    public CompletionStage<StatusServiceModel> GetStatusAsync(boolean authRequired) throws ExecutionException, InterruptedException {
        StatusServiceModel result = new StatusServiceModel(true, "Alive and well!");
        ArrayList<String> errors = new ArrayList<String>();

        if (authRequired) {
            // Check connection to Auth
            StatusResultServiceModel authResult = this.PingService(
                    authName,
                    this.servicesConfig.getUserManagementApiUrl());
            SetServiceStatus(authName, authResult, result, errors);
            result.addProperty("UserManagementApiUrl", this.servicesConfig.getUserManagementApiUrl());
        }

        // Check connection to CosmosDb
        StatusResultServiceModel storageResult = this.storageClient.ping();
        SetServiceStatus("Storage", storageResult, result, errors);

        // Check connection to TSI
        if (this.servicesConfig.getMessagesConfig().getStorageType() == StorageType.tsi) {
            StatusResultServiceModel tSIResult = this.timeSeriesClient.ping();
            SetServiceStatus("TimeSeries", tSIResult, result, errors);

            String dataAccessFqdn = this.servicesConfig.getMessagesConfig().getTimeSeriesConfig().getDataAccessFqdn();
            String environmentId = dataAccessFqdn.substring(0, dataAccessFqdn.indexOf("."));
            String tSIurl = String.format(
                    "%s?environmentId=%s&tid=%s",
                    this.servicesConfig.getMessagesConfig().getTimeSeriesConfig().getExplorerUrl(),
                    environmentId,
                    this.servicesConfig.getMessagesConfig().getTimeSeriesConfig().getAadApplicationId());
            result.addProperty("TimeSeriesUrl", tSIurl);
        }

        // Check connection to StorageAdapter
        StatusResultServiceModel storageAdapterResult = this.PingService(
                storageAdapterName,
                this.servicesConfig.getKeyValueStorageUrl());
        SetServiceStatus(storageAdapterName, storageAdapterResult, result, errors);
        result.addProperty("StorageAdapterApiUrl", this.servicesConfig.getKeyValueStorageUrl());

        // Check connection to Diagnostics
        StatusResultServiceModel diagnosticsResult = this.PingService(
                diagnosticsName,
                this.servicesConfig.getDiagnosticsConfig().getApiUrl());
        // Note: Overall simulation service status is independent of diagnostics service
        // Hence not using SetServiceStatus on diagnosticsResult
        result.addDependency(diagnosticsName, diagnosticsResult);

        if (errors.size() > 0) {
            result.setStatus(new StatusResultServiceModel(false, String.join("; ", errors)));
        }

        log.info("Service status request" + result.toString());

        return CompletableFuture.completedFuture(result);
    }

    private void SetServiceStatus(
            String dependencyName,
            StatusResultServiceModel serviceResult,
            StatusServiceModel result,
            ArrayList<String> errors
    ) {
        if (!serviceResult.getIsHealthy()) {
            errors.add(dependencyName + " check failed");
            result.getStatus().setIsHealthy(false);
        }
        result.addDependency(dependencyName, serviceResult);
    }

    private StatusResultServiceModel PingService(String serviceName, String serviceURL) {
        StatusResultServiceModel result = new StatusResultServiceModel(false, serviceName + " check failed");

        try {
            WSResponse response = this.wsRequestBuilder
                    .prepareRequest(serviceURL + "/status")
                    .get()
                    .toCompletableFuture()
                    .get();

            if (response.getStatus() != 200) {
                result.setMessage("Status code: " + response.getStatus() + ", Response: " + response.getBody());
            } else {
                ObjectMapper mapper = new ObjectMapper();
                StatusServiceModel data = mapper.readValue(response.getBody(), StatusServiceModel.class);
                result.setStatusResultServiceModel(data.getStatus());
            }

        } catch (Exception e) {
            this.log.error(e.getMessage());
        }

        return result;
    }
}
