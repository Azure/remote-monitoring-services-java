// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services;

import com.google.inject.ImplementedBy;

import java.util.concurrent.CompletionStage;

@ImplementedBy(KeyValueClient.class)
public interface IKeyValueClient {
    CompletionStage<Status> pingAsync();
}
