// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.runtime;

import java.util.List;

public interface IServicesConfig {

    String getAzureMapsKey();

    String getSeedTemplate();

    String getStorageAdapterApiUrl();

    String getDeviceSimulationApiUrl();

    String getTelemetryApiUrl();

    String getUserManagementApiUrl();
}
