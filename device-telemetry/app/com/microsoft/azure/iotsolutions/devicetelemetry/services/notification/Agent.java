package com.microsoft.azure.iotsolutions.devicetelemetry.services.notification;
import com.microsoft.azure.eventhubs.EventPosition;
import com.microsoft.azure.eventprocessorhost.EventProcessorHost;
import com.microsoft.azure.eventprocessorhost.EventProcessorOptions;
import com.microsoft.azure.eventprocessorhost.IEventProcessorFactory;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IBlobStorageConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServicesConfig;
import play.Logger;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;

public class Agent implements IAgent {
    /* Will be removed eventually when replaced in config file */
    /*private String EhConnectionString = "Endpoint=sb://eventhubnamespace-f3pvd.servicebus.windows.net/;SharedAccessKeyName=NotificationSystem;SharedAccessKey=W8C1Y/ZoBglooXxc1O1r2y5QBl7sa0nIwrYRl5h5YhA=;EntityPath=notificationsystem";
    private String EhEntityPath = "notificationsystem";
    private String StorageContainerName = "anothersystem";
    private String StorageAccountName = "aayushdemo";
    private String StorageAccountKey = "qIFS9KOWkR+GUymNElgeGGQhwvATW5SNRii4R4OTWYi0aiT/JrIFnnLyJlUVigyIoNzr5TR9utGwZoK2ffioAw==";
    private String StorageConnectionString = String.format("DefaultEndpointsProtocol=https;AccountName=%s;AccountKey=%s", StorageAccountName, StorageAccountKey);*/

    private Logger logger;
    private IServicesConfig servicesConfig;
    private IBlobStorageConfig blobStorageConfig;
    private IEventProcessorFactory notificationEventProcessorFactory;
    private IEventProcessorHostWrapper eventProcessorHostWrapper;
    private EventProcessorOptions eventProcessorOptions;

    public Agent(
            Logger logger,
            IServicesConfig servicesConfig,
            IBlobStorageConfig blobStorageConfig,
            IEventProcessorHostWrapper eventProcessorHostWrapper,
            IEventProcessorFactory notificationEventProcessorFactory
    ){
        this.logger = logger;
        this.servicesConfig = servicesConfig;
        this.blobStorageConfig = blobStorageConfig;
        this.eventProcessorHostWrapper = eventProcessorHostWrapper;
        this.notificationEventProcessorFactory = notificationEventProcessorFactory;
    }

    @Override
    public CompletionStage runAsync(){
        this.logger.info("Notification system running");
        try{
            this.logger.info("Notification system running");
            setUpEventHubAsync(); // how to make this call await? .get() doesn't work, add logging before and after async, not sure if this will work rn if not completely timed async
            this.logger.info("Notification system exiting");
            return CompletableFuture.completedFuture(true);
        } catch (Exception e){
            throw new CompletionException(e);
        }
        //.thenApply((Void v) -> this.logger.info("Notification system exiting"));
    }

    private CompletionStage setUpEventHubAsync(){
        /*try {
            EventProcessorHost host = new EventProcessorHost(EventProcessorHost.createHostName("defaultString"), EhEntityPath, "default", EhConnectionString, StorageConnectionString, StorageContainerName);
            eventProcessorOptions = new EventProcessorOptions();
            eventProcessorOptions.setInitialPositionProvider(partitionId -> EventPosition.fromEnqueuedTime(Instant.now()));

            host.registerEventProcessorFactory(this.notificationEventProcessorFactory, eventProcessorOptions).get();
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            this.logger.error("Received error setting up event hub. Will not receive updates from devices");
            throw new CompletionException(e);
        }*/
        try {
            String storageConnectionString = String
                    .format("DefaultEndpointsProtocol=https;AccountName=%s;AccountKey=%s;EndpointSuffix=%s",
                            this.blobStorageConfig.getAccountName(),
                            this.blobStorageConfig.getAccountKey(),
                            this.blobStorageConfig.getEndpointSuffix());
            EventProcessorHost host = this.eventProcessorHostWrapper.createEventProcessorHost(
                    this.servicesConfig.getEventHubName(),
                    "default-consumer-group-name",
                    this.servicesConfig.getEventHubConnectionString(),
                    storageConnectionString,
                    this.blobStorageConfig.getEventHubContainer()
            );
            eventProcessorOptions = new EventProcessorOptions();
            eventProcessorOptions.setInitialPositionProvider(partitionId -> EventPosition.fromEnqueuedTime(Instant.now()));

            this.eventProcessorHostWrapper.registerEventProcessorFactoryAsync(host, this.notificationEventProcessorFactory, eventProcessorOptions);
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            throw new CompletionException(e);
        }
    }
}
