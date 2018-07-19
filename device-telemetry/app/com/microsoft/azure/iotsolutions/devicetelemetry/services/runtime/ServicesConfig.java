// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime;

/**
 * Service layer configuration
 */
public class ServicesConfig implements IServicesConfig {

    private final String storageConnectionString;

    private final String keyValueStorageUrl;

    private final StorageConfig messagesConfig;

    private final AlarmsConfig alarmsConfig;

    private final String eventHubName;

    private final String eventHubConnectionString;

    private final int eventHubOffsetTimeInMinutes;

    private final String logicAppEndPointUrl;

    private final String solutionName;

    public ServicesConfig(
            final String storageConnectionString,
            final String keyValueStorageUrl,
            final StorageConfig messagesConfig,
            final AlarmsConfig alarmsConfig,
            final String eventHubName,
            final String eventHubConnectionString,
            final int eventHubOffsetTimeInMinutes,
            final String logicAppEndPointUrl,
            final String solutionName) {
        this.storageConnectionString = storageConnectionString;
        this.keyValueStorageUrl = keyValueStorageUrl;
        this.messagesConfig = messagesConfig;
        this.alarmsConfig = alarmsConfig;
        this.eventHubName = eventHubName;
        this.eventHubConnectionString = eventHubConnectionString;
        this.eventHubOffsetTimeInMinutes = eventHubOffsetTimeInMinutes;
        this.logicAppEndPointUrl = logicAppEndPointUrl;
        this.solutionName = solutionName;
    }

    /**
     * Get storage dependency connection string
     *
     * @return storage connection string
     */
    public String getStorageConnectionString() {
        return this.storageConnectionString;
    }

    /**
     * Get key value storage dependency url
     *
     * @return url for key value storage endpoint
     */
    public String getKeyValueStorageUrl() {
        return this.keyValueStorageUrl;
    }

    public StorageConfig getMessagesStorageConfig() {
        return this.messagesConfig;
    }

    public AlarmsConfig getAlarmsStorageConfig() {
        return this.alarmsConfig;
    }

    @Override
    public String getEventHubName() {
        return this.eventHubName;
    }

    @Override
    public String getEventHubConnectionString() { return this.eventHubConnectionString; }

    @Override
    public int getEventHubOffsetTimeInMinutes() {
        return this.eventHubOffsetTimeInMinutes;
    }

    @Override
    public String getLogicAppEndPointUrl() {
        return this.logicAppEndPointUrl;
    }

    @Override
    public String getSolutionName() { return this.solutionName; }
}
