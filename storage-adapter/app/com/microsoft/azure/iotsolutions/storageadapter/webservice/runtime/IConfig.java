// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.webservice.runtime;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.storageadapter.services.exceptions.InvalidConfigurationException;
import com.microsoft.azure.iotsolutions.storageadapter.services.runtime.IServicesConfig;

@ImplementedBy(Config.class)
public interface IConfig {
    /**
     * Get the TCP port number where the service listen for requests.
     *
     * @return TCP port number
     */
    int getPort() throws InvalidConfigurationException;

    /**
     * @return Service layer configuration
     */
    IServicesConfig getServicesConfig() throws InvalidConfigurationException;
}
