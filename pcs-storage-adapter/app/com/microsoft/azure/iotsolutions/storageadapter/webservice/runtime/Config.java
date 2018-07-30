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
    private final String applicationKey = Namespace + "pcs-storage-adapter.";
    private final String portKey = applicationKey + "webservice_port";

    // Settings about an external dependency, e.g. DocumentDB
    private final String documentdbConnectionStringKey = applicationKey + "documentdb_connstring";
    private final String documentdbDatabaseKey = applicationKey + "documentdb_database";
    private final String documentdbCollectionKey = applicationKey + "documentdb_collection";
    private final String documentdbRUsKey = applicationKey + "documentdb_RUs";

    private com.typesafe.config.Config data;
    private IServicesConfig servicesConfig;

    public Config() {
        // Load `application.conf` and replace placeholders with
        // environment variables
        data = ConfigFactory.load();

        String connectionString = data.getString(documentdbConnectionStringKey);
        String database = data.getString(documentdbDatabaseKey);
        String collection = data.getString(documentdbCollectionKey);
        int dbRUs = data.getInt(documentdbRUsKey);
        this.servicesConfig = new ServicesConfig(connectionString, database, collection, dbRUs);
    }

    /**
     * Get the TCP port number where the service listen for requests.
     *
     * @return TCP port number
     */
    public int getPort() {
        return data.getInt(portKey);
    }

    /**
     * Service layer configuration
     */
    public IServicesConfig getServicesConfig() {
        return this.servicesConfig;
    }
}
