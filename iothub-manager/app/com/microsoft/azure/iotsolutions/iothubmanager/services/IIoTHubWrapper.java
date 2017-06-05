// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.sdk.iot.service.RegistryManager;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceTwin;

// TODO: documentation

@ImplementedBy(IoTHubWrapper.class)
public interface IIoTHubWrapper {
    DeviceTwin getDeviceTwinClient();

    RegistryManager getRegistryManagerClient();
}
