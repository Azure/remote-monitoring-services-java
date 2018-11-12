// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.services;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.storageadapter.services.models.StatusResultServiceModel;
import com.microsoft.azure.iotsolutions.storageadapter.services.models.StatusServiceModel;
import com.microsoft.azure.iotsolutions.storageadapter.services.runtime.IServicesConfig;
import play.Logger;

import java.util.ArrayList;

public class StatusService implements IStatusService {

    private static final Logger.ALogger log = Logger.of(StatusService.class);
    private final IKeyValueContainer keyValueContainer;
    private final IServicesConfig servicesConfig;

    @Inject
    StatusService(IKeyValueContainer keyValueContainer, IServicesConfig servicesConfig) {
        this.keyValueContainer = keyValueContainer;
        this.servicesConfig = servicesConfig;
    }

    public StatusServiceModel getStatus() {
        StatusServiceModel result = new StatusServiceModel(true, "Alive and well!");
        ArrayList<String> errors = new ArrayList<String>();

        // Check connection to CosmosDb
        StatusResultServiceModel storageResult = this.keyValueContainer.ping();
        result.setServiceStatus("Storage", storageResult, errors);

        if (errors.size() > 0) {
            result.setStatus(new StatusResultServiceModel(false, String.join("; ", errors)));
        }

        log.info("Service status request" + result.toString());
        return result;
    }
}
