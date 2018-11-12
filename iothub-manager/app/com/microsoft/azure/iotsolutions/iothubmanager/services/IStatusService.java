// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.StatusServiceModel;

@ImplementedBy(StatusService.class)
public interface IStatusService {
    /**
     * Get status of dependent services.
     *
     * @param authRequired boolean
     *
     * @return Connection StatusServiceModel
     */
    StatusServiceModel getStatus(boolean authRequired);
}
