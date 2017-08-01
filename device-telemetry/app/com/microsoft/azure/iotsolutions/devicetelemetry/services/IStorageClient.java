// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.documentdb.*;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidConfigurationException;

@ImplementedBy(StorageClient.class)
public interface IStorageClient {
    DocumentClient getDocumentClient() throws InvalidConfigurationException;

    ResourceResponse<DocumentCollection> createCollectionIfNotExists(String id) throws Exception;

    ResourceResponse<Document> upsertDocument(String colId, Object document) throws Exception;

    ResourceResponse<Document> deleteDocument(String colId, String docId) throws Exception;

    FeedResponse<Document> queryDocuments(String colId, FeedOptions queryOptions, String queryString) throws Exception;

    Status Ping();
}
