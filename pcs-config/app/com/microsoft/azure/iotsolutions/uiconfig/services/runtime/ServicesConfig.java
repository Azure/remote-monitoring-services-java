// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.runtime;

/**
 * Service layer configuration
 */
public class ServicesConfig implements IServicesConfig {

    private String storageAdapterApiUrl;

    public ServicesConfig() {
    }

    public ServicesConfig(String storageAdapterApiUrl) {
        this.storageAdapterApiUrl = storageAdapterApiUrl;
    }

    @Override
    public String getStorageAdapterApiUrl() {
        return storageAdapterApiUrl;
    }

    public void setStorageAdapterApiUrl(String storageAdapterApiUrl) {
        this.storageAdapterApiUrl = storageAdapterApiUrl;
    }
}
