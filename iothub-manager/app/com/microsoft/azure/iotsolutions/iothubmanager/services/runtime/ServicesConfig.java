// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.runtime;

import java.util.List;

/**
 * Service layer configuration
 */
public class ServicesConfig implements IServicesConfig {

    private String hubConnString;
    private String storageAdapterServiceUrl;
    private String configServiceUrl;
    private int devicePropertiesTTL;
    private int devicePropertiesRebuildTimeout;
    private List<String> devicePropertiesWhiteList;

    public ServicesConfig(final String hubConnString, final String storageAdapterServiceUrl, final String
            configServiceUrl, int devicePropertiesTTL, int devicePropertiesRebuildTimeout, List<String>
            devicePropertiesWhiteList) {
        this.hubConnString = hubConnString;
        this.storageAdapterServiceUrl = storageAdapterServiceUrl;
        this.configServiceUrl = configServiceUrl;
        this.devicePropertiesWhiteList = devicePropertiesWhiteList;
        this.devicePropertiesTTL = devicePropertiesTTL;
        this.devicePropertiesRebuildTimeout = devicePropertiesRebuildTimeout;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHubConnString() {
        return this.hubConnString;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStorageAdapterServiceUrl() {
        return storageAdapterServiceUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getConfigServiceUrl() {
        return this.configServiceUrl;
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
