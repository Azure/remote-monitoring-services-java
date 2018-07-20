// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime;

import play.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StorageConfig {

    private static final Logger.ALogger log = Logger.of(StorageConfig.class);

    private final String storageType;
    private final String documentDbConnString;
    private final String documentDbDatabase;
    private final String documentDbCollection;

    public StorageConfig(
        String storageType,
        String documentDbConnString,
        String documentDbDatabase,
        String documentDbCollection) {

        this.storageType = storageType;
        if (!this.storageType.equalsIgnoreCase("documentdb")) {
            log.error("Unknown storage type: '{}'", this.storageType);
        }

        this.documentDbConnString = documentDbConnString;
        if (this.documentDbConnString.isEmpty()) {
            log.error("DocumentDb connection string is empty");
        }

        this.documentDbDatabase = documentDbDatabase;
        if (this.documentDbDatabase.isEmpty()) {
            log.error("DocumentDb DB name is empty");
        }

        this.documentDbCollection = documentDbCollection;
        if (this.documentDbCollection.isEmpty()) {
            log.error("DocumentDb Collection name is empty");
        }
    }

    public String getStorageType() {
        return this.storageType;
    }

    public String getDocumentDbConnString() {
        return this.documentDbConnString;
    }

    public String getDocumentDbDatabase() {
        return this.documentDbDatabase;
    }

    public String getDocumentDbCollection() {
        return this.documentDbCollection;
    }

    public String getDocumentDbUri() {
        Pattern pattern = Pattern.compile(".*AccountEndpoint=(.*);.*");
        Matcher matcher = pattern.matcher(this.getDocumentDbConnString());
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            log.error("DocumentDb AccountEndpoint not found (connection string length: {})",
                this.getDocumentDbConnString().length());
            return "https://ENDPOINT-NOT-FOUND.documents.azure.com:443/";
        }
    }

    public String getDocumentDbKey() {
        Pattern pattern = Pattern.compile(".*AccountKey=(.*);");
        Matcher matcher = pattern.matcher(this.getDocumentDbConnString());
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            log.error("DocumentDb AccountKey not found (connection string length: {})",
                this.getDocumentDbConnString().length());
            return "";
        }
    }
}
