// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.services.runtime;

/**
 * Service layer configuration
 */
public class ServicesConfig implements IServicesConfig {

    private String connectionString;
    private String containerName;

    public ServicesConfig(final String connectionString, final String containerName) {
        this.connectionString = connectionString;
        this.containerName = containerName;
    }

    /**
     * Get DB path.
     *
     * @return DB path
     */
    public String getContainerName() {
        return this.containerName;
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
