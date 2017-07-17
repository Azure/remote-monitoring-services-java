// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime;

/**
 * Service layer configuration
 */
public class ServicesConfig implements IServicesConfig {

    private String storageConnString;

    public ServicesConfig(final String storageConnString) {
        this.storageConnString = storageConnString;
    }

    /**
     * Get storage dependency connection string
     *
     * @return Connection string
     */
    public String getStorageConnString() {
        return this.storageConnString;
    }
}
