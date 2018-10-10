// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime;

import play.Logger;

public class MessagesConfig {

    private static final Logger.ALogger log = Logger.of(MessagesConfig.class);

    private final String storageType;
    private final StorageConfig storageConfig;
    private final TimeSeriesConfig timeSeriesConfig;

    public MessagesConfig(
        String storageType,
        StorageConfig storageConfig,
        TimeSeriesConfig timeSeriesConfig) {

        this.storageType = storageType;
        this.storageConfig = storageConfig;
        this.timeSeriesConfig = timeSeriesConfig;
    }

    public StorageType getStorageType() {
        switch (this.storageType.toLowerCase()) {
            case "tsi":
            case "timeseries":
            case "timeseriesinsights":
                return StorageType.tsi;
            default:
               return StorageType.cosmosdb;
        }
    }

    public StorageConfig getStorageConfig() {
        return this.storageConfig;
    }

    public TimeSeriesConfig getTimeSeriesConfig() {
        return this.timeSeriesConfig;
    }
}
