// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.InvalidConfigurationException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.runtime.IServicesConfig;
import com.microsoft.azure.sdk.iot.service.IotHubConnectionStringBuilder;
import com.microsoft.azure.sdk.iot.service.RegistryManager;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceMethod;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceTwin;

import java.io.IOException;

/**
 * A wrapper for static methods in Azure IoT SDK.
 * The only logic here should be a proxy to static methods, to facilitate
 * dependency injection and unit testing.
 */
public final class IoTHubWrapper implements IIoTHubWrapper {

    private final IServicesConfig config;

    @Inject
    public IoTHubWrapper(final IServicesConfig config) {
        this.config = config;
    }

    public DeviceTwin getDeviceTwinClient() throws ExternalDependencyException {
        try {
            return DeviceTwin.createFromConnectionString(this.config.getHubConnString());
        } catch (Exception e) {
            throw new ExternalDependencyException("Can not create IoTHub connection for DeviceTwin client", e);
        }
    }

    public RegistryManager getRegistryManagerClient() throws ExternalDependencyException {
        try {
            return RegistryManager.createFromConnectionString(this.config.getHubConnString());
        } catch (Exception e) {
            throw new ExternalDependencyException("Can not create IoTHub connection for RegistryManager client", e);
        }
    }

    public DeviceMethod getDeviceMethodClient() throws ExternalDependencyException {
        try {
            return DeviceMethod.createFromConnectionString(this.config.getHubConnString());
        } catch (Exception e) {
            throw new ExternalDependencyException("Can not create IoTHub connection for DeviceMethod client", e);
        }
    }

    public String getIotHubHostName() throws InvalidConfigurationException {
        try {
            return IotHubConnectionStringBuilder.createConnectionString(this.config.getHubConnString()).getHostName();
        } catch (IOException e) {
            throw new InvalidConfigurationException("Can not parse IoTHubHostName", e);
        }
    }
}
