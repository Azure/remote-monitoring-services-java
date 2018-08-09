// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.uiconfig.services.helpers.IHttpClientWrapper;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.IServicesConfig;
import play.Logger;

import java.util.concurrent.CompletionStage;

@Singleton
public class DeviceSimulationClient implements IDeviceSimulationClient {

    private final int SimulationId = 1;
    private IHttpClientWrapper httpClient;
    private String serviceUri;
    private static final Logger.ALogger log = Logger.of(DeviceSimulationClient.class);

    @Inject
    public DeviceSimulationClient(
            IHttpClientWrapper httpClient,
            IServicesConfig config) {
        this.httpClient = httpClient;
        this.serviceUri = config.getDeviceSimulationApiUrl();
    }

    @Override
    public CompletionStage<SimulationApiModel> getSimulationAsync() throws ExternalDependencyException {
        try {
            return this.httpClient.getAsync(String.format("%s/simulations/%d", this.serviceUri, this.SimulationId), String.format("Simulation %d", this.SimulationId), SimulationApiModel.class, true);
        } catch (Exception e) {
            log.error(String.format("GetSimulationAsync failed: %s", e.getMessage()));
            throw new ExternalDependencyException("GetSimulationAsync failed");
        }
    }

    @Override
    public CompletionStage updateSimulationAsync(SimulationApiModel model) throws ExternalDependencyException {
        try {
            return this.httpClient.putAsync(String.format("%s/simulations/%d", this.serviceUri, this.SimulationId), String.format("Simulation %d", this.SimulationId), model);
        } catch (Exception e) {
            log.error(String.format("UpdateSimulationAsync failed: %s", e.getMessage()));
            throw new ExternalDependencyException("UpdateSimulationAsync failed");
        }
    }
}
