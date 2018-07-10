package com.microsoft.azure.iotsolutions.devicetelemetry.services.notification;
import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventPosition;
import com.microsoft.azure.eventhubs.PartitionReceiver;
import com.microsoft.azure.eventprocessorhost.EventProcessorHost;
import com.microsoft.azure.eventprocessorhost.EventProcessorOptions;
import com.microsoft.azure.eventprocessorhost.IEventProcessorFactory;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServicesConfig;
import play.Logger;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;

public class Agent {
    private String EhConnectionString = "Endpoint=sb://eventhubnamespace-f3pvd.servicebus.windows.net/;SharedAccessKeyName=NotificationSystem;SharedAccessKey=W8C1Y/ZoBglooXxc1O1r2y5QBl7sa0nIwrYRl5h5YhA=;EntityPath=notificationsystem";
    private String EhEntityPath = "notificationsystem";
    private String StorageContainerName = "anothersystem";
    private String StorageAccountName = "aayushdemo";
    private String StorageAccountKey = "qIFS9KOWkR+GUymNElgeGGQhwvATW5SNRii4R4OTWYi0aiT/JrIFnnLyJlUVigyIoNzr5TR9utGwZoK2ffioAw==";

    private String StorageConnectionString = String.format("DefaultEndpointsProtocol=https;AccountName=%s;AccountKey=%s", StorageAccountName, StorageAccountKey);

    private Logger logger;
    private IServicesConfig servicesConfig;
    private IEventProcessorFactory notificationEventProcessorFactory;
    private EventProcessorOptions eventProcessorOptions;

    public Agent(Logger logger, IServicesConfig servicesConfig, IEventProcessorFactory notificationEventProcessorFactory){
        this.logger = logger;
        this.servicesConfig = servicesConfig;
        this.notificationEventProcessorFactory = notificationEventProcessorFactory;
    }

    public CompletionStage runAsync(){
        this.logger.info("Notification system running");
        try{
            setUpEventHubAsync(); // how to make this call await? .get() doesn't work
            return CompletableFuture.completedFuture(true);
        } catch (Exception e){
            throw new CompletionException(e);
        }
        //setUpEventHubAsync().thenApply((Void v) -> this.logger.info("Notification system exiting"));
    }

    private CompletionStage setUpEventHubAsync(){
        try{
            EventProcessorHost host = new EventProcessorHost(EventProcessorHost.createHostName("defaultString"), EhEntityPath, "default", EhConnectionString, StorageConnectionString, StorageContainerName);
            eventProcessorOptions = new EventProcessorOptions();
            eventProcessorOptions.setInitialPositionProvider(partitionId -> EventPosition.fromEnqueuedTime(Instant.now()));

            host.registerEventProcessorFactory(this.notificationEventProcessorFactory, eventProcessorOptions).get();
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            this.logger.error("Received error setting up event hub. Will not receive updates from devices");
            throw new CompletionException(e);
        }
    }
}