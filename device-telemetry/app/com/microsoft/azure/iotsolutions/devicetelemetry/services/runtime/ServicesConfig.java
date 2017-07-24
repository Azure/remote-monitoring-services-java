// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime;

/**
 * Service layer configuration
 */
public class ServicesConfig implements IServicesConfig {

    private String storageConnectionString;

    public ServicesConfig(final String storageConnectionString) {
        this.storageConnectionString = storageConnectionString;
    }

    /**
     * Get storage dependency connection string
     *
     * @return storage connection string
     */
    public String getStorageConnectionString() {
        return this.storageConnectionString;
    }
}
