// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.services.runtime;

/**
 * Service layer configuration
 */
public class ServicesConfig implements IServicesConfig {

    private String connectionString;
    private String database;
    private String collection;
    private int dbRUs;

    public ServicesConfig(final String connectionString, final String database, final String collection, final int dbRUs) {
        this.connectionString = connectionString;
        this.database = database;
        this.collection = collection;
        this.dbRUs = dbRUs;
    }

    /**
     * Get DB path.
     *
     * @return DB path
     */
    public String getContainerName() {
        return "/dbs/" + this.database + "/colls/" + this.collection;
    }

    /**
     * Get Document connection string.
     *
     * @return Connection string
     */
    public String getDocumentDBConnectionString() {
        return this.connectionString;
    }


}
