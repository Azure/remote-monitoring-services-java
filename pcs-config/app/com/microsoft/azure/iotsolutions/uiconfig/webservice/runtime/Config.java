// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.webservice.runtime;

import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.InvalidConfigurationException;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.ServicesConfig;
import com.typesafe.config.ConfigFactory;

public class Config implements IConfig {

    private final String NAMESPACE = "com.microsoft.azure.iotsolutions.";
    private final String APPLICATION_KEY = NAMESPACE + "uiconfig.";
    private final String PORT_KEY = APPLICATION_KEY + "webservice-port";
    private final String CORS_WHITE_LIST_KEY = APPLICATION_KEY + "cors_whitelist";
    private final String STORAGE_ADAPTER_WEBSERVICE_URL = APPLICATION_KEY + "storageadapter-webservice-url";

    private IServicesConfig servicesConfig;
    private com.typesafe.config.Config data;

    /**
     * Get the TCP port number where the service listen for requests.
     *
     * @return TCP port number
     */
    public int getPort() {
        return data.getInt(PORT_KEY);
    }

    /// <summary>CORS whitelist, in form { "origins": [], "methods": [], "headers": [] }</summary>
    public String getCorsWhitelist() {
        return this.data.getString(CORS_WHITE_LIST_KEY);
    }

    /// <summary>Service layer configuration</summary>
    public IServicesConfig getServicesConfig() {
        if (this.servicesConfig != null) return this.servicesConfig;
        this.servicesConfig = new ServicesConfig(this.data.getString(STORAGE_ADAPTER_WEBSERVICE_URL));
        return servicesConfig;
    }

    public Config() throws InvalidConfigurationException {
        this.data = ConfigFactory.load();
    }
}
