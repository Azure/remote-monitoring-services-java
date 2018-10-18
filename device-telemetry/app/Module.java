// Copyright (c) Microsoft. All rights reserved.

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidConfigurationException;
import com.microsoft.azure.eventprocessorhost.IEventProcessorFactory;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.eventhub.IEventProcessorHostWrapper;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IBlobStorageConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.auth.IClientAuthConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.runtime.IConfig;

/**
 * This class is a Guice module that tells Guice how to bind several
 * diffe/opt/code/scripts/buildrent types. This Guice module is created when the Play
 * application starts.
 *
 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
public class Module extends AbstractModule {

    @Override
    public void configure() {
        // Note: this method should be empty
        // Try to use use JIT binding and @ImplementedBy instead
    }

    @Provides
    IServicesConfig provideIServicesConfig(IConfig config) throws InvalidConfigurationException {
        return config.getServicesConfig();
    }

    @Provides
    IBlobStorageConfig provideIBlobStorageConfig(IConfig config){
        return config.getBlobStorageConfig();
    }

    @Provides
    IClientAuthConfig provideIClientAuthConfig(IConfig config) {
        return config.getClientAuthConfig();
    }

    @Provides
    IEventProcessorHostWrapper provideEventProcessorHostWrapper(IConfig config) { return config.getEventProcessorHostWrapper(); }

    @Provides
    IEventProcessorFactory provideEventProcessorFactory(IConfig config) throws InvalidConfigurationException { return config.getEventProcessorFactory(); }
}
