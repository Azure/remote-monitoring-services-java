// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.eventhub;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.eventprocessorhost.EventProcessorHost;
import com.microsoft.azure.eventprocessorhost.EventProcessorOptions;
import com.microsoft.azure.eventprocessorhost.IEventProcessorFactory;

import java.util.concurrent.CompletionStage;

@ImplementedBy(EventProcessorHostWrapper.class)
public interface IEventProcessorHostWrapper {
    EventProcessorHost createEventProcessorHost(
        String eventHubPath,
        String consumerGroupName,
        String eventHubConnectionString,
        String storageConnectionString,
        String leaseContainerName);

    CompletionStage registerEventProcessorFactoryAsync(
        EventProcessorHost host,
        IEventProcessorFactory factory);

    CompletionStage registerEventProcessorFactoryAsync(
        EventProcessorHost host,
        IEventProcessorFactory factory,
        EventProcessorOptions options);
}
