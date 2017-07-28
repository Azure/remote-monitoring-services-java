// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime;

import com.google.inject.ImplementedBy;

@ImplementedBy(ServicesConfig.class)
public interface IServicesConfig {
    /**
     * Get storage dependency connection string
     *
     * @return storage connection string
     */
    String getStorageConnectionString();

    StorageConfig getMessagesStorageConfig();

    StorageConfig getAlarmsStorageConfig();
}
