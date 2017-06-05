// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.iothubmanager.services.runtime.IServicesConfig;
import com.microsoft.azure.sdk.iot.service.RegistryManager;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceTwin;

// TODO: documentation
// TODO: handle exceptions
// TODO: logging

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

    public DeviceTwin getDeviceTwinClient() {

        try {

            String conn = this.config.getHubConnString();
            return DeviceTwin.createFromConnectionString(this.config.getHubConnString());
        } catch (Exception e) {
            // TODO: better exception handling for when connection string isn't set.
            return null;
        }
    }

    public RegistryManager getRegistryManagerClient() {

        try {
            return RegistryManager.createFromConnectionString(this.config.getHubConnString());
        } catch (Exception e) {
            // TODO: better exception handling for when connection string isn't set.
            return null;
        }
    }
}
