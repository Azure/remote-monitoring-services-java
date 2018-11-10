// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.uiconfig.services.helpers.WsRequestBuilder;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.StatusResultServiceModel;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.StatusServiceModel;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.IServicesConfig;
import org.apache.http.HttpStatus;
import play.Logger;
import play.libs.ws.WSResponse;

import java.util.ArrayList;

public class StatusService implements IStatusService {

    private final String storageAdapterName = "Storage Adapter";
    private final String authName = "Auth";
    private final String telemetryName = "Device Telemetry";
    private final String simulationName = "Device Simulation";

    private final boolean ALLOW_INSECURE_SSL_SERVER = true;
    private final int timeoutMS = 10000;

    private final WsRequestBuilder wsRequestBuilder;
    private final IServicesConfig servicesConfig;

    private static final Logger.ALogger log = Logger.of(StatusService.class);

    @Inject
    StatusService(
        IServicesConfig servicesConfig,
        WsRequestBuilder wsRequestBuilder) {
        this.wsRequestBuilder = wsRequestBuilder;
        this.servicesConfig = servicesConfig;
    }

    public StatusServiceModel getStatus() {
        StatusServiceModel result = new StatusServiceModel(true, "Alive and well!");
        ArrayList<String> errors = new ArrayList<String>();

        // Check connection to Auth
        StatusResultServiceModel authResult = this.PingService(
            authName,
            this.servicesConfig.getUserManagementApiUrl());
        SetServiceStatus(authName, authResult, result, errors);
        result.addProperty("UserManagementApiUrl", this.servicesConfig.getUserManagementApiUrl());

        // Check connection to StorageAdapter
        StatusResultServiceModel storageAdapterResult = this.PingService(
            storageAdapterName,
            this.servicesConfig.getStorageAdapterApiUrl());
        SetServiceStatus(storageAdapterName, storageAdapterResult, result, errors);
        result.addProperty("StorageAdapterApiUrl", this.servicesConfig.getStorageAdapterApiUrl());

        // Check connection to Device Telemetry
        StatusResultServiceModel telemetryResult = this.PingService(
            telemetryName,
            this.servicesConfig.getTelemetryApiUrl());
        SetServiceStatus(telemetryName, telemetryResult, result, errors);
        result.addProperty("DeviceTelemetryApiUrl", this.servicesConfig.getTelemetryApiUrl());

        // Check connection to Device Simulation
        StatusResultServiceModel deviceSimulationResult = this.PingService(
            simulationName,
            this.servicesConfig.getDeviceSimulationApiUrl());
        SetServiceStatus(simulationName, deviceSimulationResult, result, errors);
        result.addProperty("DeviceSimulationApiUrl", this.servicesConfig.getDeviceSimulationApiUrl());

        result.addProperty("SeedTemplate", this.servicesConfig.getSeedTemplate());

        if (errors.size() > 0) {
            result.setStatus(new StatusResultServiceModel(false, String.join("; ", errors)));
        }

        log.info("Service status:" + result.getStatus().getMessage());

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
