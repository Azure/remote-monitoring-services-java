// Copyright (c) Microsoft. All rights reserved.

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.microsoft.azure.iotsolutions.uiconfig.services.Cache;
import com.microsoft.azure.iotsolutions.uiconfig.services.ICache;
import com.microsoft.azure.iotsolutions.uiconfig.services.ISeed;
import com.microsoft.azure.iotsolutions.uiconfig.services.Seed;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.uiconfig.webservice.runtime.IConfig;

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
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
        // global setting is not recommend for application_onStart event, PLS refer here for details :https://www.playframework.com/documentation/2.6.x/GlobalSettings
        bind(ICache.class).to(Cache.class).asEagerSingleton();
        bind(ISeed.class).to(Seed.class).asEagerSingleton();
    }

    @Provides
    IServicesConfig provideIServicesConfig(IConfig config) {
        return config.getServicesConfig();
    }
}
