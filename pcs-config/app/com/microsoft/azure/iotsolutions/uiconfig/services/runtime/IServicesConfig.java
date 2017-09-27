// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.runtime;

public interface IServicesConfig {

    String getSeedTemplate();

    String getStorageAdapterApiUrl();

    String getDeviceSimulationApiUrl();

    String getHubManagerApiUrl();

    String getTelemetryApiUrl();

    int getCacheTTL();

    int getCacheRebuildTimeout();
}
