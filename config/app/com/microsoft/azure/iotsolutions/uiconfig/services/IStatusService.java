// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.StatusServiceModel;

@ImplementedBy(StatusService.class)
public interface IStatusService {
    /**
     * Get status of dependent services.
     *
     * @return Connection StatusServiceModel
     */
    StatusServiceModel getStatus();
}
