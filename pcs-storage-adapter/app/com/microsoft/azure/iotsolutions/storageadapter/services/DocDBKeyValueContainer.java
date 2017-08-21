// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.services;

import com.google.inject.Inject;
import com.microsoft.azure.documentdb.*;
import com.microsoft.azure.iotsolutions.storageadapter.services.exceptions.CreateResourceException;
import com.microsoft.azure.iotsolutions.storageadapter.services.helpers.DocumentIdHelper;
import com.microsoft.azure.iotsolutions.storageadapter.services.helpers.QueryBuilder;
import com.microsoft.azure.iotsolutions.storageadapter.services.models.ValueServiceModel;
import com.microsoft.azure.iotsolutions.storageadapter.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.storageadapter.services.wrappers.IFactory;
import play.Logger;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DocDBKeyValueContainer implements IKeyValueContainer {

    private static final Logger.ALogger log = Logger.of(DocDBKeyValueContainer.class);
    private DocumentClient client;
    // Todo: Rename colUrl
    private String colUrl;

    @Inject
    public DocDBKeyValueContainer(IFactory<DocumentClient> clientFactory,
                                  final IServicesConfig config) throws DocumentClientException, CreateResourceException {
        this.client = clientFactory.Create();
        this.colUrl = config.getContainerName();
    }

    public ValueServiceModel get(String collectionId, String key) throws DocumentClientException {
        String docUrl = colUrl + "/docs/" + DocumentIdHelper.GenerateId(collectionId, key);
        try {
            Document response = this.client.readDocument(docUrl, new RequestOptions()).getResource();
            return new ValueServiceModel(response);
        } catch (DocumentClientException ex) {
            log.error("Error reading document: " + docUrl);
            throw ex;
        }
    }


    public java.util.Iterator<ValueServiceModel> list(String collectionId) {
        String sqlQuery = QueryBuilder.buildSQL(collectionId);
        FeedOptions queryOptions = new FeedOptions();
        queryOptions.setPageSize(-1);
        queryOptions.setEnableCrossPartitionQuery(true);
        Iterator<Document> response = this.client.queryDocuments(colUrl, sqlQuery, queryOptions).getQueryIterator();
        List<ValueServiceModel> result = new ArrayList<>();
        while (response.hasNext()) {
            Document element = response.next();
            result.add(new ValueServiceModel(element));
        }
        return result.iterator();
    }


    public ValueServiceModel create(String collectionId, String key, ValueServiceModel input) throws DocumentClientException {
        KeyValueDocument document = new KeyValueDocument(collectionId, key, input.Data);
        try {
            Document response = this.client.createDocument(colUrl, document, new RequestOptions(), true).getResource();
            return new ValueServiceModel(response);
        } catch (DocumentClientException ex) {
            log.error("Error creating document: " + colUrl + ", Key=" + key);
            throw ex;
        }
    }

    public ValueServiceModel upsert(String collectionId, String key, ValueServiceModel input) throws DocumentClientException {
        try {
            KeyValueDocument document = new KeyValueDocument(collectionId, key, input.Data);
            Document response = this.client.upsertDocument(colUrl, document, new RequestOptions(), false).getResource();
            return new ValueServiceModel(response);
        } catch (DocumentClientException ex) {
            log.error("Error upsert document: " + colUrl + ", Key=" + key);
            throw ex;
        }
    }

    public void delete(String collectionId, String key) throws DocumentClientException {
        String docUrl = colUrl + "/docs/" + DocumentIdHelper.GenerateId(collectionId, key);
        try {
            this.client.deleteDocument(docUrl, new RequestOptions());
        } catch (DocumentClientException ex) {
            log.error("Error deleting document: " + docUrl);
            throw ex;
        }
    }


    public Status ping() {
        URI response = null;
        if (this.client != null) {
            response = this.client.getReadEndpoint();
        }
        if (response != null) {
            return new Status(true, "Alive and Well!");
        } else {
            return new Status(false, "Could not connect to DocumentDb." + colUrl);
        }
    }

}
