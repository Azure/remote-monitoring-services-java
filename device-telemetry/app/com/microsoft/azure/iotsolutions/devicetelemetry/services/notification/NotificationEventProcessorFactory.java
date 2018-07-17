package com.microsoft.azure.iotsolutions.devicetelemetry.services.notification;

import com.google.inject.Inject;
import com.microsoft.azure.eventprocessorhost.IEventProcessor;
import com.microsoft.azure.eventprocessorhost.IEventProcessorFactory;
import com.microsoft.azure.eventprocessorhost.PartitionContext;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServicesConfig;
import play.Logger;

public class NotificationEventProcessorFactory implements IEventProcessorFactory {
    // private static final Logger.ALogger logger = Logger.of(NotificationEventProcessorFactory.class);
    private IServicesConfig servicesConfig;

    @Inject
    public NotificationEventProcessorFactory(IServicesConfig servicesConfig){
        this.servicesConfig = servicesConfig;
    }

    @Override
    public IEventProcessor createEventProcessor(PartitionContext context) throws Exception {
        return new NotificationEventProcessor(this.servicesConfig);
    }
}
