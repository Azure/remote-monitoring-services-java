package com.microsoft.azure.iotsolutions.devicetelemetry.services.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventprocessorhost.CloseReason;
import com.microsoft.azure.eventprocessorhost.IEventProcessor;
import com.microsoft.azure.eventprocessorhost.PartitionContext;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.models.AlarmNotificationAsaModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServicesConfig;
import play.Logger;
import play.libs.ws.WSClient;

public class NotificationEventProcessor implements IEventProcessor {
    private static final Logger.ALogger logger = Logger.of(NotificationEventProcessor.class);
    private IServicesConfig servicesConfig;
    private INotification notification;
    private WSClient client;

    @Inject
    public NotificationEventProcessor(WSClient client, IServicesConfig servicesConfig, INotification notification){
        this.client = client;
        this.servicesConfig = servicesConfig;
        this.notification = notification;
    }

    @Override
    public void onOpen(PartitionContext context) throws Exception {
        this.logger.info(String.format("Notification EventProcessor initialized. Partition: %s", context.getPartitionId()));
    }

    @Override
    public void onClose(PartitionContext context, CloseReason reason) throws Exception {
        this.logger.info(String.format("Notification EventProcessor shutting down. Parition %s, Reason: %s", context.getPartitionId(), reason.toString()));
    }

    @Override
    public void onEvents(PartitionContext context, Iterable<EventData> events) throws Exception {
        for(EventData eventData : events){
            String data = new String(eventData.getBytes(), "UTF8");
            AlarmNotificationAsaModel model = new ObjectMapper().readValue(data, AlarmNotificationAsaModel.class);
            this.notification.setAlarm(model);
            notification.executeAsync();
        }
    }

    @Override
    public void onError(PartitionContext context, Throwable error) {
        this.logger.info(String.format("Error on Partition: %s, Error: %s", context.getPartitionId(), error.getMessage()));
    }
}
