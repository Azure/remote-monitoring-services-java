// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.services;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.storageadapter.services.models.StatusResultServiceModel;
import com.microsoft.azure.iotsolutions.storageadapter.services.models.StatusServiceModel;
import com.microsoft.azure.iotsolutions.storageadapter.services.runtime.IServicesConfig;
import play.Logger;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class StatusService implements IStatusService {

    private static final Logger.ALogger log = Logger.of(StatusService.class);
    private final IKeyValueContainer keyValueContainer;
    private final IServicesConfig servicesConfig;

    @Inject
    StatusService(IKeyValueContainer keyValueContainer, IServicesConfig servicesConfig) {
        this.keyValueContainer = keyValueContainer;
        this.servicesConfig = servicesConfig;
    }

    public CompletionStage<StatusServiceModel> GetStatusAsync() {
        StatusServiceModel result = new StatusServiceModel(true, "Alive and well!");
        ArrayList<String> errors = new ArrayList<String>();

        // Check connection to CosmosDb
        StatusResultServiceModel storageResult = this.keyValueContainer.ping();
        SetServiceStatus("Storage", storageResult, result, errors);

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

}
