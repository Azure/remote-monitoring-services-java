// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.iothubmanager.services.helpers.WsRequestBuilder;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.StatusResultServiceModel;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.StatusServiceModel;
import com.microsoft.azure.iotsolutions.iothubmanager.services.runtime.IServicesConfig;
import org.apache.http.HttpStatus;
import play.Logger;
import play.libs.ws.WSResponse;

import java.util.ArrayList;

public class StatusService implements IStatusService {

    private final String storageAdapterName = "StorageAdapter";
    private final String authName = "Auth";

    private final boolean ALLOW_INSECURE_SSL_SERVER = true;
    private final int timeoutMS = 10000;

    private final WsRequestBuilder wsRequestBuilder;
    private final IServicesConfig servicesConfig;
    private final IoTHubWrapper ioTHubWrapper;

    private static final Logger.ALogger log = Logger.of(StatusService.class);

    @Inject
    StatusService(
        IServicesConfig servicesConfig,
        WsRequestBuilder wsRequestBuilder,
        IoTHubWrapper ioTHubWrapper
    ) {
        this.wsRequestBuilder = wsRequestBuilder;
        this.servicesConfig = servicesConfig;
        this.ioTHubWrapper = ioTHubWrapper;
    }

    public StatusServiceModel getStatus(boolean authRequired) {
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

        // Check connection to StorageAdapter
        StatusResultServiceModel storageAdapterResult = this.PingService(
            storageAdapterName,
            this.servicesConfig.getStorageAdapterServiceUrl());
        SetServiceStatus(storageAdapterName, storageAdapterResult, result, errors);
        result.addProperty("StorageAdapterApiUrl", this.servicesConfig.getStorageAdapterServiceUrl());

        // Check connection to IoTHub
        StatusResultServiceModel ioTHubResult = this.ioTHubWrapper.ping();
        SetServiceStatus("IoTHub", ioTHubResult, result, errors);

        if (errors.size() > 0) {
            result.setStatus(new StatusResultServiceModel(false, String.join("; ", errors)));
        }

        log.info("Service status request" + result.toString());

        return result;
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

            if (response.getStatus() != HttpStatus.SC_OK) {
                result.setMessage("Status code: " + response.getStatus() + ", Response: " + response.getBody());
            } else {
                ObjectMapper mapper = new ObjectMapper();
                StatusServiceModel data = mapper.readValue(response.getBody(), StatusServiceModel.class);
                result.setStatusResultServiceModel(data.getStatus());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return result;
    }
}
