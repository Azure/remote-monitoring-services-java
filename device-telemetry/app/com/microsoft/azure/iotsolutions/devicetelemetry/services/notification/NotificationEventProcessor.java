// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventprocessorhost.CloseReason;
import com.microsoft.azure.eventprocessorhost.IEventProcessor;
import com.microsoft.azure.eventprocessorhost.PartitionContext;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.models.AlarmNotificationAsaModel;
import play.Logger;

public class NotificationEventProcessor implements IEventProcessor {
    private static final Logger.ALogger logger = Logger.of(NotificationEventProcessor.class);
    private INotification notification;

    @Inject
    public NotificationEventProcessor(INotification notification){
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
