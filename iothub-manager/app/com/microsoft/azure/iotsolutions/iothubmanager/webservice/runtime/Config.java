// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.runtime;

import com.microsoft.azure.iotsolutions.iothubmanager.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.iothubmanager.services.runtime.ServicesConfig;
import com.typesafe.config.ConfigFactory;

// TODO: documentation

public class Config implements IConfig {

    // Namespace applied to all the custom configuration settings
    private final String NAMESPACE = "com.microsoft.azure.iotsolutions.";

    // Settings about this application
    private final String APPLICATION_KEY = NAMESPACE + "iothub-manager-java.";
    private final String PORT_KEY = APPLICATION_KEY + "webservice-port";
    private final String HOST_NAME_KEY = APPLICATION_KEY + "webservice-hostname";
    private final String IOTHUB_CONNSTRING_KEY = APPLICATION_KEY + "iothub.connstring";

    private com.typesafe.config.Config data;
    private IServicesConfig servicesConfig;

    public Config() {
        // Load `application.conf` and replace placeholders with
        // environment variables
        data = ConfigFactory.load();
    }

    /**
     * Get the TCP port number where the service listen for requests.
     *
     * @return TCP port number
     */
    public int getPort() {
        return data.getInt(PORT_KEY);
    }

    /**
     * Get the hostname where the service listen for requests, e.g. 0.0.0.0 when
     * listening to all the network adapters.
     *
     * @return Hostname or IP address
     */
    public String getHostname() {
        return data.getString(HOST_NAME_KEY);
    }

    /**
     * Service layer configuration
     */
    public IServicesConfig getServicesConfig() {
        if (this.servicesConfig != null) return this.servicesConfig;

        String cs = data.getString(IOTHUB_CONNSTRING_KEY);
        this.servicesConfig = new ServicesConfig(cs);
        return this.servicesConfig;
    }
}
