// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.runtime;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidConfigurationException;
import com.microsoft.azure.eventprocessorhost.IEventProcessorFactory;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.eventhub.IEventProcessorHostWrapper;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IBlobStorageConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.auth.IClientAuthConfig;

@ImplementedBy(Config.class)
public interface IConfig {

    /**
     * Service layer configuration
     */
    IServicesConfig getServicesConfig() throws InvalidConfigurationException;

    /**
     * Client authorization configuration
     */
    IClientAuthConfig getClientAuthConfig();

    /**
     * Storage configuration
     */
    IBlobStorageConfig getBlobStorageConfig();

    /**
     * Event processor host wrapper
     */
    IEventProcessorHostWrapper getEventProcessorHostWrapper();

    /**
     * Event processor factory
     */
    IEventProcessorFactory getEventProcessorFactory()  throws InvalidConfigurationException;
}
