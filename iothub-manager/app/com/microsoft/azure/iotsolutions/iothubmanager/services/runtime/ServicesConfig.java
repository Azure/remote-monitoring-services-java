// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.runtime;

/**
 * Service layer configuration
 */
public class ServicesConfig implements IServicesConfig {

    private String hubConnString;

    public ServicesConfig(final String hubConnString) {
        this.hubConnString = hubConnString;
    }

    /**
     * Get Azure IoT Hub connection string.
     *
     * @return Connection string
     */
    public String getHubConnString() {
        return this.hubConnString;
    }
}
