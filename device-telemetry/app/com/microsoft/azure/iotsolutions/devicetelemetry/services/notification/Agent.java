// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.notification;
import com.google.inject.Inject;
import com.microsoft.azure.eventprocessorhost.EventProcessorHost;
import com.microsoft.azure.eventprocessorhost.EventProcessorOptions;
import com.microsoft.azure.eventprocessorhost.IEventProcessorFactory;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.eventhub.IEventProcessorHostWrapper;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IBlobStorageConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServicesConfig;
import play.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;

public class Agent implements IAgent {
    private static final Logger.ALogger logger = Logger.of(Agent.class);
    private IServicesConfig servicesConfig;
    private IBlobStorageConfig blobStorageConfig;
    private IEventProcessorFactory notificationEventProcessorFactory;
    private IEventProcessorHostWrapper eventProcessorHostWrapper;
    private EventProcessorOptions eventProcessorOptions;
    private static final String DEFAULT = "$Default";

    @Inject
    public Agent(
            IServicesConfig servicesConfig,
            IBlobStorageConfig blobStorageConfig,
            IEventProcessorHostWrapper eventProcessorHostWrapper,
            IEventProcessorFactory notificationEventProcessorFactory )
    {
        this.servicesConfig = servicesConfig;
        this.blobStorageConfig = blobStorageConfig;
        this.eventProcessorHostWrapper = eventProcessorHostWrapper;
        this.notificationEventProcessorFactory = notificationEventProcessorFactory;
    }

    @Override
    public CompletionStage runAsync() {
        this.logger.info("Notification system running");
        try {
            setUpEventHubAsync().thenRun(() -> this.logger.info("Set up eventhub complete"));
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            this.logger.error(e.getMessage());
            throw new CompletionException(e);
        }
    }

    private CompletionStage setUpEventHubAsync() {
        try {
            String storageConnectionString = String
                    .format("DefaultEndpointsProtocol=https;AccountName=%s;AccountKey=%s;EndpointSuffix=%s",
                            this.blobStorageConfig.getAccountName(),
                            this.blobStorageConfig.getAccountKey(),
                            this.blobStorageConfig.getEndpointSuffix());
            EventProcessorHost host = this.eventProcessorHostWrapper.createEventProcessorHost(
                    this.servicesConfig.getEventHubName(),
                    DEFAULT,
                    this.servicesConfig.getEventHubConnectionString(),
                    storageConnectionString,
                    this.blobStorageConfig.getEventHubContainer()
            );
            this.eventProcessorHostWrapper.registerEventProcessorFactoryAsync(host, this.notificationEventProcessorFactory);
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            throw new CompletionException(e);
        }
    }
}
