// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.external;

import com.google.inject.ImplementedBy;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.CompletionStage;

@ImplementedBy(DiagnosticsClient.class)
public interface IDiagnosticsClient {
    CompletionStage<Void> logEventAsync(String eventName);

    CompletionStage<Void> logEventAsync(String eventName, Dictionary<String, Object> eventProperties);
}
