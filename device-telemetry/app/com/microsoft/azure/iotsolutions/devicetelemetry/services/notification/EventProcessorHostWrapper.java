// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.notification;

import com.microsoft.azure.eventprocessorhost.EventProcessorHost;
import com.microsoft.azure.eventprocessorhost.EventProcessorOptions;
import com.microsoft.azure.eventprocessorhost.IEventProcessorFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;

public class EventProcessorHostWrapper implements IEventProcessorHostWrapper {
    private static final String DEFAULT_STRING = "defaultString";

    public EventProcessorHostWrapper() {
        // empty constructor
    }

    @Override
    public EventProcessorHost createEventProcessorHost(
            String eventHubPath,
            String consumerGroupName,
            String eventHubConnectionString,
            String storageConnectionString,
            String leaseContainerName)
    {
        return new EventProcessorHost(
                EventProcessorHost.createHostName(DEFAULT_STRING),
                eventHubPath,
                consumerGroupName,
                eventHubConnectionString,
                storageConnectionString,
                leaseContainerName);
    }

    @Override
    public CompletionStage registerEventProcessorFactoryAsync(EventProcessorHost host, IEventProcessorFactory factory) {
        try {
            host.registerEventProcessorFactory(factory);
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            throw new CompletionException(e);
        }
    }

    @Override
    public CompletionStage registerEventProcessorFactoryAsync(
            EventProcessorHost host,
            IEventProcessorFactory factory,
            EventProcessorOptions options) {
        try {
            host.registerEventProcessorFactory(factory, options);
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            throw new CompletionException(e);
        }
    }
}
