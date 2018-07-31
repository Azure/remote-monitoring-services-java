// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime;

public class BlobStorageConfig implements IBlobStorageConfig {
    public String accountName;
    public String accountKey;
    public String endpointSuffix;
    public String eventHubContainer;

    public BlobStorageConfig(String accountName, String accountKey, String endpointSuffix, String eventHubContainer) {
        this.accountName = accountName;
        this.accountKey = accountKey;
        this.endpointSuffix = endpointSuffix;
        this.eventHubContainer = eventHubContainer;
    }

    @Override
    public String getAccountName() {
        return this.accountName;
    }

    @Override
    public String getAccountKey() {
        return this.accountKey;
    }

    @Override
    public String getEndpointSuffix() {
        return this.endpointSuffix;
    }

    @Override
    public String getEventHubContainer() {
        return this.eventHubContainer;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setAccountKey(String accountKey) {
        this.accountKey = accountKey;
    }

    public void setEndpointSuffix(String endpointSuffix) {
        this.endpointSuffix = endpointSuffix;
    }

    public void setEventHubContainer(String eventHubContainer) {
        this.eventHubContainer = eventHubContainer;
    }
}
