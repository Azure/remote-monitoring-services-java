// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.runtime;

/**
 * Service layer configuration
 */
public class ServicesConfig implements IServicesConfig {

    private String hubConnString;
    private String configServiceUrl;

    public ServicesConfig(final String hubConnString, final String configServiceUrl) {
        this.hubConnString = hubConnString;
        this.configServiceUrl = configServiceUrl;
    }

    /**
     * Get Azure IoT Hub connection string.
     *
     * @return Connection string
     */
    public String getHubConnString() {
        return this.hubConnString;
    }

    /**
     * Get Config service URL.
     *
     * @return Config service URL
     */
    public String getConfigServiceUrl() {
        return this.configServiceUrl;
    }
}
