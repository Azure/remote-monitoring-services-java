// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.runtime;

public interface IServicesConfig {

    String getStorageAdapterApiUrl();

    String getDeviceSimulationApiUrl();

    String getHubManagerApiUrl();

    int getCacheTTL();

    int getCacheRebuildTimeout();
}
