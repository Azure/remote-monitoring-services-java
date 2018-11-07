// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.storage.storageAdapter;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.StatusServiceModel;

import java.util.concurrent.CompletionStage;

@ImplementedBy(StorageAdapterClient.class)
public interface IStorageAdapterClient {
}
