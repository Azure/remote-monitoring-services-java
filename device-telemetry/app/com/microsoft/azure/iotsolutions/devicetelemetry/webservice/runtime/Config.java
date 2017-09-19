// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.runtime;

import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.ServicesConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.StorageConfig;
import com.typesafe.config.ConfigFactory;

// TODO: documentation
// TODO: handle exceptions

public class Config implements IConfig {

    // Namespace applied to all the custom configuration settings
    private final String Namespace = "com.microsoft.azure.iotsolutions.";

    // Settings about this application
    private final String ApplicationKey = Namespace + "telemetry.";
    private final String PortKey = ApplicationKey + "webservice-port";

    // Storage dependency settings
    private final String StorageKey = ApplicationKey + "documentdb.";
    private final String StorageConnStringKey = StorageKey + "connstring";

    // Storage adapter webservice settings
    private final String KeyValueStorageKey = ApplicationKey + "storageadapter.";
    private final String KeyValueStorageUrlKey = KeyValueStorageKey + "url";

    private final String messagesStorageTypeKey = ApplicationKey + "messages.storageType";
    private final String messagesDocDbConnStringKey = ApplicationKey + "messages.documentDb.connString";
    private final String messagesDocDbDatabaseKey = ApplicationKey + "messages.documentDb.database";
    private final String messagesDocDbCollectionKey = ApplicationKey + "messages.documentDb.collection";

    private final String alarmsStorageTypeKey = ApplicationKey + "alarms.storageType";
    private final String alarmsDocDbConnStringKey = ApplicationKey + "alarms.documentDb.connString";
    private final String alarmsDocDbDatabaseKey = ApplicationKey + "alarms.documentDb.database";
    private final String alarmsDocDbCollectionKey = ApplicationKey + "alarms.documentDb.collection";

    private com.typesafe.config.Config data;
    private IServicesConfig servicesConfig;

    public Config() {
        this.data = ConfigFactory.load();
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

        if (this.servicesConfig != null) return this.servicesConfig;

        String storageConnectionString = this.data.getString(StorageConnStringKey);
        String keyValueStorageUrl = this.data.getString(KeyValueStorageUrlKey);

        StorageConfig messagesConfig = new StorageConfig(
            data.getString(messagesStorageTypeKey).toLowerCase(),
            data.getString(messagesDocDbConnStringKey),
            data.getString(messagesDocDbDatabaseKey),
            data.getString(messagesDocDbCollectionKey));

        StorageConfig alarmsConfig = new StorageConfig(
            data.getString(alarmsStorageTypeKey).toLowerCase(),
            data.getString(alarmsDocDbConnStringKey),
            data.getString(alarmsDocDbDatabaseKey),
            data.getString(alarmsDocDbCollectionKey));

        this.servicesConfig = new ServicesConfig(
            storageConnectionString,
            keyValueStorageUrl,
            messagesConfig,
            alarmsConfig);

        return this.servicesConfig;
    }
}
