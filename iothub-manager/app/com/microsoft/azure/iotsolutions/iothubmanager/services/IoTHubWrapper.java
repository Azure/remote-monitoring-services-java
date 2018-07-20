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
import com.microsoft.azure.sdk.iot.service.jobs.JobClient;
import play.Logger;

import java.io.IOException;

/**
 * A wrapper for static methods in Azure IoT SDK.
 * The only logic here should be a proxy to static methods, to facilitate
 * dependency injection and unit testing.
 */
public final class IoTHubWrapper implements IIoTHubWrapper {

    private static Logger.ALogger log = Logger.of(IoTHubWrapper.class);

    private final IServicesConfig config;

    @Inject
    public IoTHubWrapper(final IServicesConfig config) {
        this.config = config;
    }

    public DeviceTwin getDeviceTwinClient() throws ExternalDependencyException {
        try {
            return DeviceTwin.createFromConnectionString(this.config.getHubConnString());
        } catch (Exception e) {
            String message = "Can not create IoTHub connection for DeviceTwin client";
            log.error(message, e);
            throw new ExternalDependencyException(message, e);
        }
    }

    public RegistryManager getRegistryManagerClient() throws ExternalDependencyException {
        try {
            return RegistryManager.createFromConnectionString(this.config.getHubConnString());
        } catch (Exception e) {
            String message = "Can not create IoTHub connection for RegistryManager client";
            log.error(message, e);
            throw new ExternalDependencyException(message, e);
        }
    }

    public DeviceMethod getDeviceMethodClient() throws ExternalDependencyException {
        try {
            return DeviceMethod.createFromConnectionString(this.config.getHubConnString());
        } catch (Exception e) {
            String message = "Can not create IoTHub connection for DeviceMethod client";
            log.error(message, e);
            throw new ExternalDependencyException(message, e);
        }
    }

    public String getIotHubHostName() throws InvalidConfigurationException {
        try {
            return IotHubConnectionStringBuilder.createConnectionString(this.config.getHubConnString()).getHostName();
        } catch (IOException e) {
            String message = "Can not parse IoTHubHostName";
            log.error(message, e);
            throw new InvalidConfigurationException(message, e);
        }
    }

    public JobClient getJobClient() throws ExternalDependencyException {
        try {
            return JobClient.createFromConnectionString(this.config.getHubConnString());
        } catch (Exception e) {
            String message = "Can not create IoTHub connection for Job client";
            log.error(message, e);
            throw new ExternalDependencyException(message, e);
        }
    }
}
