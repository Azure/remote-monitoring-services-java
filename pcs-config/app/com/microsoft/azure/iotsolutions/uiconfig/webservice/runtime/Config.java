// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.webservice.runtime;

import com.google.inject.Singleton;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.InvalidConfigurationException;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.ServicesConfig;
import com.typesafe.config.ConfigFactory;

// TODO: documentation
// TODO: handle exceptions

@Singleton
public class Config implements IConfig {

    private final String Namespace = "com.microsoft.azure.iotsolutions.";
    private final String ApplicationKey = Namespace + "UIConfig.";
    private final String PortKey = ApplicationKey + "webservice_port";
    private final String CorsWhitelistKey = ApplicationKey + "cors_whitelist";
    private final String StorageAdapterUrlKey = ApplicationKey + "StorageAdapter.webservice_url";
    private IServicesConfig servicesConfig;
    private com.typesafe.config.Config data;

    /// Web service listening port</summary>
    public int getPort() {
        return this.data.getInt(PortKey);
    }

    /// <summary>CORS whitelist, in form { "origins": [], "methods": [], "headers": [] }</summary>
    public String getCorsWhitelist() {
        return this.data.getString(CorsWhitelistKey);
    }

    /// <summary>Service layer configuration</summary>
    public IServicesConfig getServicesConfig() {
        if (this.servicesConfig != null) return this.servicesConfig;
        this.servicesConfig = new ServicesConfig(this.data.getString(StorageAdapterUrlKey));
        return servicesConfig;
    }

    public Config() throws InvalidConfigurationException {
        this.data = ConfigFactory.load();
    }
}
