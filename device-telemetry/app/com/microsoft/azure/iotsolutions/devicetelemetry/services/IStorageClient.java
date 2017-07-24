// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidConfigurationException;

@ImplementedBy(StorageClient.class)
public interface IStorageClient {
    DocumentClient getDocumentClient() throws InvalidConfigurationException;

    StatusTuple Ping();
}
