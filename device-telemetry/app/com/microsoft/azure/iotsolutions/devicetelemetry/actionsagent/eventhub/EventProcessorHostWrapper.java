// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.eventhub;

import com.microsoft.azure.eventprocessorhost.EventProcessorHost;
import com.microsoft.azure.eventprocessorhost.EventProcessorOptions;
import com.microsoft.azure.eventprocessorhost.IEventProcessorFactory;

import java.util.concurrent.CompletionStage;

public class EventProcessorHostWrapper implements IEventProcessorHostWrapper {

    private static final String DEFAULT_HOST_NAME_PREFIX = "actions-host";

    public EventProcessorHostWrapper() {
    }

    @Override
    public EventProcessorHost createEventProcessorHost(
        String eventHubPath,
        String consumerGroupName,
        String eventHubConnectionString,
        String storageConnectionString,
        String leaseContainerName) {
        return new EventProcessorHost(
            EventProcessorHost.createHostName(DEFAULT_HOST_NAME_PREFIX),
            eventHubPath,
            consumerGroupName,
            eventHubConnectionString,
            storageConnectionString,
            leaseContainerName);
    }

    @Override
    public CompletionStage registerEventProcessorFactoryAsync(
        EventProcessorHost host,
        IEventProcessorFactory factory) {
        return host.registerEventProcessorFactory(factory);
    }

    @Override
    public CompletionStage registerEventProcessorFactoryAsync(
        EventProcessorHost host,
        IEventProcessorFactory factory,
        EventProcessorOptions options) {
        return host.registerEventProcessorFactory(factory, options);
    }
}
