package com.microsoft.azure.iotsolutions.devicetelemetry.services.notification;

import com.google.inject.Inject;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventprocessorhost.CloseReason;
import com.microsoft.azure.eventprocessorhost.IEventProcessor;
import com.microsoft.azure.eventprocessorhost.PartitionContext;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.Rules;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServicesConfig;
import play.Logger;

public class NotificationEventProcessor implements IEventProcessor {
    private static final Logger.ALogger logger = Logger.of(NotificationEventProcessor.class);
    private IServicesConfig servicesConfig;

    @Inject
    public NotificationEventProcessor(IServicesConfig servicesConfig){
        this.servicesConfig = servicesConfig;
    }

    @Override
    public void onOpen(PartitionContext context) throws Exception {
        this.logger.info(String.format("Notification EventProcessor initialized. Partition: %s", context.getPartitionId()));
    }

    @Override
    public void onClose(PartitionContext context, CloseReason reason) throws Exception {
        this.logger.info(String.format("Notification EventProcessor shutting down. Partition: %s", context.getPartitionId()));
    }

    @Override
    public void onEvents(PartitionContext context, Iterable<EventData> events) throws Exception {
        for(EventData eventData : events){
            String data = new String(eventData.getBytes(), "UTF8");
            Logger.info(data);
            Logger.info("this was one data string");
        }
    }

    @Override
    public void onError(PartitionContext context, Throwable error) {
        this.logger.info(String.format("Error on Partition: %s, Error: %s", context.getPartitionId(), error.getMessage()));
    }
}
