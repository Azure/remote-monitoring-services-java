// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.storage;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.documentdb.*;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.Status;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidConfigurationException;

import java.util.ArrayList;

@ImplementedBy(StorageClient.class)
public interface IStorageClient {
    DocumentClient getDocumentClient() throws InvalidConfigurationException;

    ResourceResponse<DocumentCollection> createCollectionIfNotExists(String databaseName, String id) throws Exception;

    Document upsertDocument(String databaseName, String colId, Object document) throws Exception;

    Document deleteDocument(String databaseName, String colId, String docId) throws Exception;

    ArrayList<Document> queryDocuments(String databaseName, String colId, FeedOptions queryOptions, String queryString, int skip) throws Exception;

    Status ping();
}
