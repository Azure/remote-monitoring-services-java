// Copyright (c) Microsoft. All rights reserved

package com.microsoft.azure.iotsolutions.devicetelemetry.services.storage;

import com.google.inject.Inject;
import com.microsoft.azure.documentdb.*;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.Status;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidConfigurationException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServicesConfig;
import play.Logger;
import play.mvc.Http;

import java.net.URI;
import java.util.ArrayList;

public class StorageClient implements IStorageClient {

    private static final Logger.ALogger log = Logger.of(StorageClient.class);

    private final IServicesConfig servicesConfig;

    private String storageHostName;
    private String storagePrimaryKey;

    private DocumentClient client;

    @Inject
    public StorageClient(final IServicesConfig config) throws Exception {
        this.servicesConfig = config;
        parseConnectionString();
        this.client = getDocumentClient();
    }

    // returns existing document client, creates document client if null
    public DocumentClient getDocumentClient() throws InvalidConfigurationException {
        if (this.client == null) {
            this.client = new DocumentClient(
                storageHostName,
                storagePrimaryKey,
                ConnectionPolicy.GetDefault(),
                ConsistencyLevel.Session);

            if (this.client == null) {
                // TODO add logging if connection fails (don't log connection string)
                log.error("Could not connect to DocumentClient");
                throw new InvalidConfigurationException("Could not connect to DocumentClient");
            }
        }

        return this.client;
    }

    @Override
    public ResourceResponse<DocumentCollection> createCollectionIfNotExists(String databaseName, String id) throws Exception {
        DocumentCollection collectionInfo = new DocumentCollection();
        RangeIndex index = Index.Range(DataType.String, -1);
        collectionInfo.setIndexingPolicy(new IndexingPolicy(new Index[]{index}));
        collectionInfo.setId(id);

        // Azure Cosmos DB collections can be reserved with throughput specified in request units/second.
        // Here we create a collection with 400 RU/s.
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.setOfferThroughput(400);
        String dbUrl = "/dbs/" + databaseName;
        String colUrl = dbUrl + "/colls/" + id;
        boolean create = false;
        ResourceResponse<DocumentCollection> response = null;

        try {
            response = this.client.readCollection(colUrl, requestOptions);
        } catch (DocumentClientException dcx) {
            if (dcx.getStatusCode() == Http.Status.NOT_FOUND) {
                create = true;
            } else {
                log.error("Error reading collection: {}. Exception: {}", id, dcx);
            }
        }

        if (create) {
            try {
                response = this.client.createCollection(dbUrl, collectionInfo, requestOptions);
            } catch (Exception ex) {
                log.error("Error creating collection. Id: {}, dbUrl: {}, collection: {}. Exception: {}", id, dbUrl, collectionInfo, ex);
                throw ex;
            }
        }

        return response;
    }

    @Override
    public Document upsertDocument(String databaseName, String colId, final Object document) throws Exception {
        String colUrl = String.format("/dbs/%s/colls/%s", databaseName, colId);
        try {
            return this.client.upsertDocument(colUrl, document, new RequestOptions(), false).getResource();
        } catch (Exception ex) {
            log.error("Error upserting document collection: {}. Exception: {}", colId, ex);
            throw ex;
        }
    }

    @Override
    public Document deleteDocument(String databaseName, String colId, String docId) throws Exception {
        String docUrl = String.format("/dbs/%s/colls/%s/docs/%s", databaseName, colId, docId);
        try {
            return this.client.deleteDocument(docUrl, new RequestOptions()).getResource();
        } catch (Exception ex) {
            log.error("Error deleting document in collection: {}. Exception: {}", colId, ex);
            throw ex;
        }
    }

    @Override
    public ArrayList<Document> queryDocuments(String databaseName, String colId, FeedOptions queryOptions, String queryString, int skip) throws Exception {
        if (queryOptions == null) {
            queryOptions = new FeedOptions();
            queryOptions.setEnableCrossPartitionQuery(true);
            queryOptions.setEnableScanInQuery(true);
        }

        ArrayList<Document> docs = new ArrayList<>();
        String continuationToken = null;
        String collectionLink = String.format("/dbs/%s/colls/%s", databaseName, colId);
        do {
            FeedResponse<Document> queryResults = this.client.queryDocuments(
                collectionLink,
                queryString,
                queryOptions);

            for (Document doc : queryResults.getQueryIterable()) {
                if (skip == 0) {
                    docs.add(doc);
                } else {
                    skip--;
                }
            }

            continuationToken = queryResults.getResponseContinuation();
            queryOptions.setRequestContinuation(continuationToken);
        } while (continuationToken != null);

        return docs;
    }

    @Override
    public Status ping() {
        URI response = null;

        if (this.client != null) {
            response = this.client.getReadEndpoint();
        }

        if (response != null) {
            return new Status(
                true,
                "Alive and Well!");
        } else {
            return new Status(
                false,
                "Could not reach storage service" +
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
