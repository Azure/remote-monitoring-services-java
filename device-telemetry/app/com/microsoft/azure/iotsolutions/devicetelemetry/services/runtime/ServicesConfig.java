// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime;

/**
 * Service layer configuration
 */
public class ServicesConfig implements IServicesConfig {

    private final String storageConnectionString;

    private final StorageConfig messagesConfig;

    private final StorageConfig alarmsConfig;

    public ServicesConfig(
        final String storageConnectionString,
        StorageConfig messagesConfig,
        StorageConfig alarmsConfig) {
        this.storageConnectionString = storageConnectionString;
        this.messagesConfig = messagesConfig;
        this.alarmsConfig = alarmsConfig;
    }

    /**
     * Get storage dependency connection string
     *
     * @return storage connection string
     */
    public String getStorageConnectionString() {
        return this.storageConnectionString;
    }

    public StorageConfig getMessagesStorageConfig() {
        return this.messagesConfig;
    }

    public StorageConfig getAlarmsStorageConfig() {
        return this.alarmsConfig;
    }
}
