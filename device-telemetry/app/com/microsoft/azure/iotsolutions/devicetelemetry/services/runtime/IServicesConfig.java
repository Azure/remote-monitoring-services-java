// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime;

import com.google.inject.ImplementedBy;

@ImplementedBy(ServicesConfig.class)
public interface IServicesConfig {
    /**
     * Get storage dependency connection string
     */
    String getStorageConnectionString();

    /**
     * Get key value storage dependency url
     */
    String getKeyValueStorageUrl();

    StorageConfig getMessagesStorageConfig();

    AlarmsConfig getAlarmsStorageConfig();

    String getEventHubName();

    String getEventHubConnectionString();

    int getEventHubOffsetTimeInMinutes();

    String getLogicAppEndPointUrl();

    String getSolutionName();
}
