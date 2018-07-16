package com.microsoft.azure.iotsolutions.devicetelemetry.services.notification;

import com.microsoft.azure.eventprocessorhost.IEventProcessor;
import com.microsoft.azure.eventprocessorhost.IEventProcessorFactory;
import com.microsoft.azure.eventprocessorhost.PartitionContext;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServicesConfig;
import play.Logger;

public class NotificationEventProcessorFactory implements IEventProcessorFactory {
    private Logger logger;
    private IServicesConfig servicesConfig;

    public NotificationEventProcessorFactory(Logger logger, IServicesConfig servicesConfig){
        this.logger = logger;
        this.servicesConfig = servicesConfig;
    }

    @Override
    public IEventProcessor createEventProcessor(PartitionContext context) throws Exception {
        return new NotificationEventProcessor(this.logger, this.servicesConfig);
    }
}
