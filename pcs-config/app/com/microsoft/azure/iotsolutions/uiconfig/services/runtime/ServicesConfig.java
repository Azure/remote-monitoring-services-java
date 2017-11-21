// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.runtime;

import java.util.List;

/**
 * Service layer configuration
 */
public class ServicesConfig implements IServicesConfig {

    private String bingMapKey;
    private String seedTemplate;
    private String storageAdapterApiUrl;
    private String deviceSimulationApiUrl;
    private String telemetryApiUrl;
    private String hubManagerApiUrl;
    private int cacheTTL;
    private int cacheRebuildTimeout;
    private List<String> cacheWhiteList;

    public String getBingMapKey() {
        return bingMapKey;
    }

    public void setBingMapKey(String bingMapKey) {
        this.bingMapKey = bingMapKey;
    }


    @Override
    public String getTelemetryApiUrl() {
        return telemetryApiUrl;
    }

    public void setTelemetryApiUrl(String telemetryApiUrl) {
        this.telemetryApiUrl = telemetryApiUrl;
    }

    public ServicesConfig() {
    }

    public ServicesConfig(String telemetryApiUrl, String storageAdapterApiUrl, String deviceSimulationApiUrl,
                          String hubManagerApiUrl, int cacheTTL, int cacheRebuildTimeout, String seedTemplate, String bingMapKey, List<String> cacheWhiteList) {
        this.storageAdapterApiUrl = storageAdapterApiUrl;
        this.deviceSimulationApiUrl = deviceSimulationApiUrl;
        this.hubManagerApiUrl = hubManagerApiUrl;
        this.cacheTTL = cacheTTL;
        this.cacheRebuildTimeout = cacheRebuildTimeout;
        this.seedTemplate = seedTemplate;
        this.telemetryApiUrl = telemetryApiUrl;
        this.bingMapKey = bingMapKey;
        this.cacheWhiteList=cacheWhiteList;
    }

    @Override
    public String getSeedTemplate() {
        return seedTemplate;
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

    @Override
    public List<String> getCacheWhiteList() {
        return cacheWhiteList;
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
