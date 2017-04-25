// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.template.runtime;

import com.google.inject.AbstractModule;

public class InjectionModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IConfig.class).to(Config.class);
    }
}
