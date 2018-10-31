// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.eventhub;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.eventprocessorhost.EventProcessorHost;
import com.microsoft.azure.eventprocessorhost.IEventProcessorFactory;

import java.util.concurrent.CompletionStage;

/**
 * The wrapper of IEventProcessorHost to encapsulate the operation
 * of creating new instance and registering factory for event processor
 */
@ImplementedBy(EventProcessorHostWrapper.class)
public interface IEventProcessorHostWrapper {

    /**
     * Create a new event processor host
     *
     * @param eventHubPath the eventHub path to listen on
     * @param consumerGroupName the consumer group name of the eventHub
     * @param eventHubConnectionString the eventHub connection string
     * @param storageConnectionString the blob storage connection string for eventHub checkpoint
     * @param leaseContainerName the lease container name of blob storage for checkpoint
     * @return a new EventProcessHost instance
     */
    EventProcessorHost createEventProcessorHost(
        String eventHubPath,
        String consumerGroupName,
        String eventHubConnectionString,
        String storageConnectionString,
        String leaseContainerName);

    /**
     * Create a new event processor host
     *
     * @param host The event processor host to be registered
     * @param factory The event processor factory to create new instance of event processor host
     * @return the new CompletionStage
     */
    CompletionStage registerEventProcessorFactoryAsync(
        EventProcessorHost host,
        IEventProcessorFactory factory);
}
