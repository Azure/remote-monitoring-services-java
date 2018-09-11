// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime;

import play.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StorageConfig {

    private static final Logger.ALogger log = Logger.of(StorageConfig.class);

    private final String cosmosDbConnString;
    private final String cosmosDbDatabase;
    private final String cosmosDbCollection;

    public StorageConfig(
        String cosmosDbConnString,
        String cosmosDbDatabase,
        String cosmosDbCollection) {

        this.cosmosDbConnString = cosmosDbConnString;
        if (this.cosmosDbConnString.isEmpty()) {
            log.error("CosmosDb connection string is empty");
        }

        this.cosmosDbDatabase = cosmosDbDatabase;
        if (this.cosmosDbDatabase.isEmpty()) {
            log.error("CosmosDb DB name is empty");
        }

        this.cosmosDbCollection = cosmosDbCollection;
        if (this.cosmosDbCollection.isEmpty()) {
            log.error("CosmosDb Collection name is empty");
        }
    }

    public String getCosmosDbConnString() {
        return this.cosmosDbConnString;
    }

    public String getCosmosDbDatabase() {
        return this.cosmosDbDatabase;
    }

    public String getCosmosDbCollection() {
        return this.cosmosDbCollection;
    }

    public String getCosmosDbUri() {
        Pattern pattern = Pattern.compile(".*AccountEndpoint=(.*);.*");
        Matcher matcher = pattern.matcher(this.getCosmosDbConnString());
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            log.error("CosmosDb AccountEndpoint not found (connection string length: {})",
                this.getCosmosDbConnString().length());
            return "https://ENDPOINT-NOT-FOUND.documents.azure.com:443/";
        }
    }

    public String getCosmosDbKey() {
        Pattern pattern = Pattern.compile(".*AccountKey=(.*);");
        Matcher matcher = pattern.matcher(this.getCosmosDbConnString());
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            log.error("CosmosDb AccountKey not found (connection string length: {})",
                this.getCosmosDbConnString().length());
            return "";
        }
    }
}
