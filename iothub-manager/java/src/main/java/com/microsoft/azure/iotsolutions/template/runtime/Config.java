// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.template.runtime;

import com.typesafe.config.ConfigFactory;

public class Config implements IConfig {

    private com.typesafe.config.Config data;

    public Config() {
        data = ConfigFactory.load();
    }

    @Override
    public int getWebServicePort() {
        return data.getInt("com.microsoft.azure.iotsolutions.template.webservice-port");
    }

    @Override
    public String getHostname() {
        return data.getString("com.microsoft.azure.iotsolutions.template.webservice-hostname");
    }
}
