// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.storage;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.Status;

import java.util.concurrent.CompletionStage;

@ImplementedBy(KeyValueClient.class)
public interface IKeyValueClient {
    CompletionStage<Status> pingAsync();
}
