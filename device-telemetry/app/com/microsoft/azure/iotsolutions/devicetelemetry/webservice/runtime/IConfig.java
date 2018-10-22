// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.runtime;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidConfigurationException;
import com.microsoft.azure.eventprocessorhost.IEventProcessorFactory;
import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.eventhub.IEventProcessorHostWrapper;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServiceConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.auth.IClientAuthConfig;

@ImplementedBy(Config.class)
public interface IConfig {

    /**
     * Service layer configuration
     */
    IServiceConfig getServicesConfig() throws InvalidConfigurationException;

    /**
     * Client authorization configuration
     */
    IClientAuthConfig getClientAuthConfig();

    /**
     * Event processor host wrapper
     */
    IEventProcessorHostWrapper getEventProcessorHostWrapper();

    /**
     * Event processor factory
     */
    IEventProcessorFactory getEventProcessorFactory()  throws InvalidConfigurationException;
}
