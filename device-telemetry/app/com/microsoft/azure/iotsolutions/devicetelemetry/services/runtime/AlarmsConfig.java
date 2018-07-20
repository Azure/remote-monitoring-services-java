// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime;

public class AlarmsConfig {

    private final StorageConfig storageConfig;
    private final int maxDeleteRetries;

    public AlarmsConfig(
            String storageType,
            String documentDbConnString,
            String documentDbDatabase,
            String documentDbCollection,
            int maxDeleteRetries) {
        this.storageConfig = new StorageConfig(storageType, documentDbConnString, documentDbDatabase, documentDbCollection);
        this.maxDeleteRetries = maxDeleteRetries;
    }

    public int getMaxDeleteRetries() {
        return this.maxDeleteRetries;
    }

    public StorageConfig getStorageConfig() {
        return this.storageConfig;
    }
}

