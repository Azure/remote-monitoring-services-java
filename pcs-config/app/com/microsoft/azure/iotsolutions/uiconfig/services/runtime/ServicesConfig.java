// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.runtime;

/**
 * Service layer configuration
 */
public class ServicesConfig implements IServicesConfig {

    private String storageAdapterApiUrl;
    private String deviceSimulationApiUrl;
    private String hubManagerApiUrl;
    private int cacheTTL;
    private int cacheRebuildTimeout;

    public ServicesConfig() {
    }

    public ServicesConfig(String storageAdapterApiUrl, String deviceSimulationApiUrl,
                          String hubManagerApiUrl, int cacheTTL, int cacheRebuildTimeout) {
        this.storageAdapterApiUrl = storageAdapterApiUrl;
        this.deviceSimulationApiUrl = deviceSimulationApiUrl;
        this.hubManagerApiUrl = hubManagerApiUrl;
        this.cacheTTL = cacheTTL;
        this.cacheRebuildTimeout = cacheRebuildTimeout;
    }

    @Override
    public String getStorageAdapterApiUrl() {
        return storageAdapterApiUrl;
    }

    @Override
    public String getDeviceSimulationApiUrl() {
        return this.deviceSimulationApiUrl;
    }

    @Override
    public String getHubManagerApiUrl() {
        return hubManagerApiUrl;
    }

    @Override
    public int getCacheTTL() {
        return cacheTTL;
    }

    @Override
    public int getCacheRebuildTimeout() {
        return cacheRebuildTimeout;
    }

    public void setStorageAdapterApiUrl(String storageAdapterApiUrl) {
        this.storageAdapterApiUrl = storageAdapterApiUrl;
    }

    public void setDeviceSimulationApiUrl(String deviceSimulationApiUrl) {
        this.deviceSimulationApiUrl = deviceSimulationApiUrl;
    }

    public void setHubManagerApiUrl(String hubManagerApiUrl) {
        this.hubManagerApiUrl = hubManagerApiUrl;
    }

    public void setCacheTTL(int cacheTTL) {
        this.cacheTTL = cacheTTL;
    }

    public void setCacheRebuildTimeout(int cacheRebuildTimeout) {
        this.cacheRebuildTimeout = cacheRebuildTimeout;
    }
}
