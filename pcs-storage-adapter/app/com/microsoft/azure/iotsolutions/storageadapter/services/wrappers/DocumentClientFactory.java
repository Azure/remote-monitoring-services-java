// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.services.wrappers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.microsoft.azure.documentdb.*;
import com.microsoft.azure.iotsolutions.storageadapter.services.exceptions.CreateResourceException;
import com.microsoft.azure.iotsolutions.storageadapter.services.exceptions.InvalidConfigurationException;
import com.microsoft.azure.iotsolutions.storageadapter.webservice.runtime.Config;
import play.Logger;
import play.mvc.Http;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class DocumentClientFactory implements IFactory<DocumentClient> {

    private static final Logger.ALogger log = Logger.of(DocumentClientFactory.class);
    private String docDbEndpoint;
    private String docDbKey;
    private String databaseName;
    private String collectionId;
    private boolean hasCreatingError = false;

    @Inject
    public DocumentClientFactory(Config config) throws InvalidConfigurationException {
        String connectString = config.getServicesConfig().getDocumentDBConnectionString();
        this.docDbEndpoint = getDocumentDbUri(connectString);
        this.docDbKey = getDocumentDbKey(connectString);
        String colUrl = config.getServicesConfig().getContainerName();
        parseCollectionLink(colUrl);

        if (this.docDbEndpoint.isEmpty() ||
                this.docDbKey.isEmpty() ||
                this.databaseName.isEmpty() ||
                this.collectionId.isEmpty()
                ) {
            log.error("Config: 'docDbEndpoint'=" + docDbEndpoint + ", 'docDbKey'=" + docDbKey + ", 'databaseName'=" + databaseName + ", 'collectionId'=" + collectionId);
            throw new InvalidConfigurationException("DocumentDB configuration error");
        }
    }

    private void createDatabase(DocumentClient client, String databaseName) throws DocumentClientException {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.setOfferThroughput(400);
        try {
            Database databaseInfo = new Database();
            databaseInfo.setId(databaseName);
            client.createDatabase(databaseInfo, requestOptions);
        } catch (DocumentClientException ex) {
            this.hasCreatingError = true;
            log.error("Error creating database: " + databaseName);
            throw ex;
        }
    }

    private void createDatabaseIfNotExists(DocumentClient client, String databaseName) throws DocumentClientException {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.setOfferThroughput(400);
        String databaseLink = String.format("/dbs/%s", databaseName);
        try {
            client.readDatabase(databaseLink, requestOptions);
        } catch (DocumentClientException ex) {
            if (ex.getStatusCode() == Http.Status.NOT_FOUND) {
                createDatabase(client, databaseName);
            } else {
                log.error("Error reading database: " + databaseLink);
                throw ex;
            }
        }
    }

    private void createCollection(DocumentClient client, String databaseName, String collectionId) throws DocumentClientException {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.setOfferThroughput(400);
        String databaseLink = String.format("/dbs/%s", databaseName);
        try {
            DocumentCollection collectionInfo = new DocumentCollection();
            RangeIndex index = Index.Range(DataType.String, -1);
            collectionInfo.setIndexingPolicy(new IndexingPolicy(new Index[]{index}));
            collectionInfo.setId(collectionId);
            client.createCollection(databaseLink, collectionInfo, requestOptions);
        } catch (DocumentClientException ex) {
            this.hasCreatingError = true;
            log.error("Error creating collection: " + databaseName + "/colls/" + collectionId);
            throw ex;
        }

    }

    private void createCollectionIfNotExists(DocumentClient client, String databaseName, String collectionId) throws DocumentClientException {
        String collectionLink = String.format("/dbs/%s/colls/%s", databaseName, collectionId);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.setOfferThroughput(400);
        try {
            client.readCollection(collectionLink, requestOptions);
        } catch (DocumentClientException ex) {
            if (ex.getStatusCode() == Http.Status.NOT_FOUND) {
                createCollection(client, databaseName, collectionId);
            } else {
                log.error("Error reading collection: " + collectionLink);
                throw ex;
            }
        }
    }

    private void parseCollectionLink(String collectionLink) {
        Pattern pattern = Pattern.compile("/dbs/(.*)/colls/(.*)");
        Matcher matcher = pattern.matcher(collectionLink);
        if (matcher.find()) {
            this.databaseName = matcher.group(1);
            this.collectionId = matcher.group(2);
        }
    }

    private static String getDocumentDbUri(String DocumentDbConnString) {
        Pattern pattern = Pattern.compile(".*AccountEndpoint=(.*);.*");
        Matcher matcher = pattern.matcher(DocumentDbConnString);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }

    private String getDocumentDbKey(String DocumentDbConnString) {
        Pattern pattern = Pattern.compile(".*AccountKey=(.*);");
        Matcher matcher = pattern.matcher(DocumentDbConnString);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }

    public DocumentClient Create() throws DocumentClientException, CreateResourceException {
        DocumentClient client = new DocumentClient(docDbEndpoint, docDbKey, ConnectionPolicy.GetDefault(), ConsistencyLevel.Session);
        // Do not try to create new if we know it will fail.
        if (!this.hasCreatingError) {
            createDatabaseIfNotExists(client, databaseName);
            createCollectionIfNotExists(client, databaseName, collectionId);
        } else {
            throw new CreateResourceException("Could not create DocumentDB.");
        }
        return client;
    }
}
