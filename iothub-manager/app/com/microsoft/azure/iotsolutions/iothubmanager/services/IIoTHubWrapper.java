// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.*;
import com.microsoft.azure.sdk.iot.service.RegistryManager;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceMethod;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceTwin;
import com.microsoft.azure.sdk.iot.service.jobs.JobClient;

/**
 * A wrapper to create IoTHub Registry and DeviceTwin client to facilitate
 * access Azure IoT SDK.
 */
@ImplementedBy(IoTHubWrapper.class)
public interface IIoTHubWrapper {
    DeviceTwin getDeviceTwinClient() throws ExternalDependencyException;

    RegistryManager getRegistryManagerClient() throws ExternalDependencyException;

    DeviceMethod getDeviceMethodClient() throws ExternalDependencyException;

    String getIotHubHostName() throws InvalidConfigurationException;

    JobClient getJobClient() throws ExternalDependencyException;
}
