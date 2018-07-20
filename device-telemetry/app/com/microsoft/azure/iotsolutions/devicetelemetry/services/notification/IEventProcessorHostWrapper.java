// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.notification;

import com.microsoft.azure.eventprocessorhost.EventProcessorHost;
import com.microsoft.azure.eventprocessorhost.EventProcessorOptions;
import com.microsoft.azure.eventprocessorhost.IEventProcessorFactory;

import java.util.concurrent.CompletionStage;

public interface IEventProcessorHostWrapper {
    public EventProcessorHost createEventProcessorHost(
            String eventHubPath,
            String consumerGroupName,
            String eventHubConnectionString,
            String storageConnectionString,
            String leaseContainerName);

    public CompletionStage registerEventProcessorFactoryAsync(
            EventProcessorHost host,
            IEventProcessorFactory factory);

    public CompletionStage registerEventProcessorFactoryAsync(
            EventProcessorHost host,
            IEventProcessorFactory factory,
            EventProcessorOptions options);
}
