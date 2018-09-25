// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.runtime;

import java.util.List;

public interface IServicesConfig {

    /**
     * Get Azure IoT Hub connection string.
     *
     * @return Connection string
     */
    String getHubConnString();

    /**
     * Get Storage Adapter service URL.
     *
     * @return Storage Adapter service URL
     */
    String getStorageAdapterServiceUrl();

    /**
     * Get Config service URL.
     *
     * @return Config service URL
     */
    String getConfigServiceUrl();

    int getDevicePropertiesTTL();
    int getDevicePropertiesRebuildTimeout();
    List<String> getDevicePropertiesWhiteList();
}
