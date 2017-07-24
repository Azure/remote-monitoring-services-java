// Copyright (c) Microsoft. All rights reserved

package com.microsoft.azure.iotsolutions.devicetelemetry.services;

import com.google.inject.Inject;
import com.microsoft.azure.documentdb.ConnectionPolicy;
import com.microsoft.azure.documentdb.ConsistencyLevel;
import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidConfigurationException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServicesConfig;

import java.net.URI;

public class StorageClient implements IStorageClient {

    private final IServicesConfig servicesConfig;

    private String storageHostName;
    private String storagePrimaryKey;

    private static DocumentClient documentClient;

    @Inject
    public StorageClient(final IServicesConfig config) throws InvalidConfigurationException {
        this.servicesConfig = config;
        parseConnectionString();
        documentClient = getDocumentClient();
    }

    // returns existing document client, creates document client if null
    public DocumentClient getDocumentClient() throws InvalidConfigurationException {
        if (documentClient == null) {
            documentClient = new DocumentClient(
                storageHostName,
                storagePrimaryKey,
                ConnectionPolicy.GetDefault(),
                ConsistencyLevel.Session);

            if (documentClient == null) {
                // TODO add logging if connection fails (don't log connection string)
                throw new InvalidConfigurationException("Could not connect to DocumentClient");
            }
        }

        return documentClient;
    }

    public StatusTuple Ping() {
        URI response = null;

        if (documentClient != null) {
            response = documentClient.getReadEndpoint();
        }

        if (response != null) {
            return new StatusTuple(
                true,
                "Alive and Well!");
        } else {
            return new StatusTuple(
                false,
                "Could not connect to DocumentDb. " +
                    "Check connection string");
        }
    }

    // splits connection string into hostname and primary key
    private void parseConnectionString() throws InvalidConfigurationException {
        final String HOST_ID = "AccountEndpoint=";
        final String KEY_ID = "AccountKey=";

        String connectionString = servicesConfig.getStorageConnectionString();

        if (!connectionString.contains(";") ||
            !connectionString.contains(HOST_ID) ||
            !connectionString.contains(KEY_ID)) {
            // TODO add logging for connection string error (don't log conn string)
            throw new InvalidConfigurationException("Connection string format: " +
                "accepted format \"AccountEndpoint={value};AccountKey={value}\"");
        }

        String[] params = connectionString.split(";");
        if (params.length > 1) {
            this.storageHostName = params[0].substring(
                params[0].indexOf(HOST_ID) + HOST_ID.length());

            this.storagePrimaryKey = params[1].substring(
                params[1].indexOf(KEY_ID) + KEY_ID.length());
        } else {
            // TODO add logging for connection string error (don't log conn string)
            throw new InvalidConfigurationException("Connection string format error");
        }
    }
}
