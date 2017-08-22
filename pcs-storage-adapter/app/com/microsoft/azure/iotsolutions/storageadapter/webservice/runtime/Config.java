// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.webservice.runtime;

import com.google.inject.Singleton;
import com.microsoft.azure.iotsolutions.storageadapter.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.storageadapter.services.runtime.ServicesConfig;
import com.typesafe.config.ConfigFactory;

// TODO: documentation
// TODO: handle exceptions

@Singleton
public class Config implements IConfig {

    // Namespace applied to all the custom configuration settings
    private final String Namespace = "com.microsoft.azure.iotsolutions.";

    // Settings about this application
    private final String ApplicationKey = Namespace + "pcs-storage-adapter-java.";
    private final String PortKey = ApplicationKey + "webservice-port";
    private final String ContainerNameKey = ApplicationKey + "container_name";

    // Settings about an external dependency, e.g. DocumentDB
    private final String StorageKey = ApplicationKey + "storage.";
    private final String StorageConnectionStringKey = StorageKey + "connection_string";


    private com.typesafe.config.Config data;
    private IServicesConfig servicesConfig;

    public Config() {
        // Load `application.conf` and replace placeholders with
        // environment variables
        data = ConfigFactory.load();

        String connectionString = data.getString(StorageConnectionStringKey);
        String containerName = data.getString(ContainerNameKey);
        this.servicesConfig = new ServicesConfig(connectionString, containerName);
    }

    /**
     * Get the TCP port number where the service listen for requests.
     *
     * @return TCP port number
     */
    public int getPort() {
        return data.getInt(PortKey);
    }

    /**
     * Service layer configuration
     */
    public IServicesConfig getServicesConfig() {
        return this.servicesConfig;
    }
}
