// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.webservice.runtime;

import com.microsoft.azure.iotsolutions.storageadapter.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.storageadapter.services.runtime.ServicesConfig;
import com.typesafe.config.ConfigFactory;

// TODO: documentation
// TODO: handle exceptions

public class Config implements IConfig {

    private final String Namespace = "com.microsoft.azure.iotsolutions.";
    private final String Application = "StorageAdapter.";

    private com.typesafe.config.Config data;
    private IServicesConfig servicesConfig;

    public Config() {
        // Load `application.conf` and replace placeholders with
        // environment variables
        data = ConfigFactory.load();

        this.servicesConfig = new ServicesConfig();
    }

    /**
     * Get the TCP port number where the service listen for requests.
     *
     * @return TCP port number
     */
    public int getPort() {
        return data.getInt(Namespace + Application + "webservice-port");
    }

    /**
     * Get the hostname where the service listen for requests, e.g. 0.0.0.0 when
     * listening to all the network adapters.
     *
     * @return Hostname or IP address
     */
    public String getHostname() {
        return data.getString(Namespace + Application + "webservice-hostname");
    }

    /**
     * Service layer configuration
     */
    public IServicesConfig getServicesConfig() {
        return this.servicesConfig;
    }
}
