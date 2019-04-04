// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.runtime;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.InvalidConfigurationException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.auth.IClientAuthConfig;

@ImplementedBy(Config.class)
public interface IConfig {

    /**
     * Get the TCP port number where the service listen for requests.
     *
     * @return TCP port number
     */
    int getPort() throws InvalidConfigurationException;

    /**
     * Service layer configuration
     */
    IServicesConfig getServicesConfig() throws InvalidConfigurationException;

    /**
     * Client authorization configuration
     */
    IClientAuthConfig getClientAuthConfig() throws InvalidConfigurationException;
}
