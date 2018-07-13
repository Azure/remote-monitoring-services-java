package com.microsoft.azure.iotsolutions.devicetelemetry.services.notification;

import com.microsoft.azure.eventprocessorhost.IEventProcessor;
import com.microsoft.azure.eventprocessorhost.IEventProcessorFactory;
import com.microsoft.azure.eventprocessorhost.PartitionContext;
import play.Logger;

public class NotificationEventProcessorFactory implements IEventProcessorFactory {
    private Logger logger;

    public NotificationEventProcessorFactory(Logger logger){
        this.logger = logger;
    }

    @Override
    public IEventProcessor createEventProcessor(PartitionContext context) throws Exception {
        return new NotificationEventProcessor(this.logger);
    }
}
