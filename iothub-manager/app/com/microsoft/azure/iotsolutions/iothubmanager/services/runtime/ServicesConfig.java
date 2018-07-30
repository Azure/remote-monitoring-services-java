// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.runtime;

import java.util.List;

/**
 * Service layer configuration
 */
public class ServicesConfig implements IServicesConfig {

    private String hubConnString;
    private String storageAdapterServiceUrl;
    private int devicePropertiesTTL;
    private int devicePropertiesRebuildTimeout;
    private List<String> devicePropertiesWhiteList;

    public ServicesConfig(final String hubConnString, final String storageAdapterServiceUrl,
                          int devicePropertiesTTL, int devicePropertiesRebuildTimeout, List<String> devicePropertiesWhiteList) {
        this.hubConnString = hubConnString;
        this.storageAdapterServiceUrl = storageAdapterServiceUrl;
        this.devicePropertiesWhiteList = devicePropertiesWhiteList;
        this.devicePropertiesTTL = devicePropertiesTTL;
        this.devicePropertiesRebuildTimeout = devicePropertiesRebuildTimeout;
    }

    /**
     * Get Azure IoT Hub connection string.
     *
     * @return Connection string
     */
    public String getHubConnString() {
        return this.hubConnString;
    }

    /**
     * Get Storage Adapter service URL.
     *
     * @return Storage Adapter service URL
     */
    @Override
    public String getStorageAdapterServiceUrl() {
        return storageAdapterServiceUrl;
    }

    @Override
    public int getDevicePropertiesTTL() {
        return devicePropertiesTTL;
    }

    @Override
    public int getDevicePropertiesRebuildTimeout() {
        return devicePropertiesRebuildTimeout;
    }

    @Override
    public List<String> getDevicePropertiesWhiteList() {
        return devicePropertiesWhiteList;
    }
}
