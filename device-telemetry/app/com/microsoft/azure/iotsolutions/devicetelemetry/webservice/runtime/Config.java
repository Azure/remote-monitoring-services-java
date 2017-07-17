// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.runtime;


import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.ServicesConfig;
import com.typesafe.config.ConfigFactory;

// TODO: documentation
// TODO: handle exceptions

public class Config implements IConfig {

    // Namespace applied to all the custom configuration settings
    private final String Namespace = "com.microsoft.azure.iotsolutions.";

    // Settings about this application
    private final String ApplicationKey = Namespace + "devicetelemetry.";
    private final String PortKey = ApplicationKey + "webservice-port";

    // Storage dependency settings
    private final String StorageKey = Namespace + "documentdb.";
    private final String StorageConnStringKey = StorageKey + "connstring";

    private com.typesafe.config.Config data;
    private IServicesConfig servicesConfig;

    public Config() {
        // Load `application.conf` and replace placeholders with
        // environment variables
        this.data = ConfigFactory.load();

        String cs = this.data.getString(StorageConnStringKey);
        this.servicesConfig = new ServicesConfig(cs);
    }

    /**
     * Get the TCP port number where the service listen for requests.
     *
     * @return TCP port number
     */
    public int getPort() {
        return this.data.getInt(PortKey);
    }

    /**
     * Service layer configuration
     */
    public IServicesConfig getServicesConfig() {
        return this.servicesConfig;
    }
}
