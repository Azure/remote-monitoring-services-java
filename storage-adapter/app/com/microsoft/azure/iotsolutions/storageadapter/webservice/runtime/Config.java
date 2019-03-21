// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.webservice.runtime;

import com.google.inject.Singleton;
import com.microsoft.azure.iotsolutions.storageadapter.services.exceptions.InvalidConfigurationException;
import com.microsoft.azure.iotsolutions.storageadapter.services.runtime.ConfigData;
import com.microsoft.azure.iotsolutions.storageadapter.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.storageadapter.services.runtime.ServicesConfig;

// TODO: documentation
// TODO: handle exceptions
@Singleton
public class Config implements IConfig {

    // Namespace applied to all the custom configuration settings
    private final String Namespace = "com.microsoft.azure.iotsolutions.";

    // Settings about this application
    private final String applicationKey = Namespace + "pcs-storage-adapter.";
    private final String portKey = applicationKey + "webservicePort";

    // Settings about an external dependency, e.g. DocumentDB
    private final String documentdbConnectionStringKey = applicationKey + "documentDBConnectionString";
    private final String documentdbDatabaseKey = applicationKey + "documentDBDatabase";
    private final String documentdbCollectionKey = applicationKey + "documentDBCollection";
    private final String documentdbRUsKey = applicationKey + "documentDBRUs";

    private ConfigData data;
    private IServicesConfig servicesConfig;

    public Config() throws InvalidConfigurationException {
        // Load `application.conf` and replace placeholders with
        // environment variables
        data = new ConfigData(applicationKey);

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
    public int getPort() throws InvalidConfigurationException {
        return data.getInt(portKey);
    }

    /**
     * Service layer configuration
     */
    public IServicesConfig getServicesConfig() throws InvalidConfigurationException {
        return this.servicesConfig;
    }
}
