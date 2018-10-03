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

    int getDevicePropertiesTTL();
    int getDevicePropertiesRebuildTimeout();
    List<String> getDevicePropertiesWhiteList();
}
