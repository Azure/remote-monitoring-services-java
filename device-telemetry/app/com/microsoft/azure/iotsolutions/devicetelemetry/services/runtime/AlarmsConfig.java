// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime;

public class AlarmsConfig {

    private final String storageType;
    private final StorageConfig storageConfig;
    private final int maxDeleteRetries;

    public AlarmsConfig(
            String storageType,
            StorageConfig storageConfig,
            int maxDeleteRetries) {

        this.storageType = storageType;
        this.storageConfig = storageConfig;
        this.maxDeleteRetries = maxDeleteRetries;
    }

    public int getMaxDeleteRetries() {
        return this.maxDeleteRetries;
    }

    public StorageConfig getStorageConfig() {
        return this.storageConfig;
    }
}

