// Copyright (c) Microsoft. All rights reserved.

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.microsoft.azure.iotsolutions.uiconfig.services.*;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.uiconfig.webservice.auth.IClientAuthConfig;
import com.microsoft.azure.iotsolutions.uiconfig.webservice.runtime.IConfig;

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.
 * <p>
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
        // global setting is not recommend for application_onStart event, PLS refer here for details :https://www.playframework.com/documentation/2.6.x/GlobalSettings
        bind(IRecurringTasks.class).to(RecurringTasks.class).asEagerSingleton();
    }

    @Provides
    IServicesConfig provideIServicesConfig(IConfig config) {
        return config.getServicesConfig();
    }

    @Provides
    IClientAuthConfig provideIClientAuthConfig(IConfig config) {
        return config.getClientAuthConfig();
    }
}
