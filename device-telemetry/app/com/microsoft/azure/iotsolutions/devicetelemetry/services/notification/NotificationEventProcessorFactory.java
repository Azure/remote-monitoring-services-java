// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.notification;

import com.google.inject.Inject;
import com.microsoft.azure.eventprocessorhost.IEventProcessor;
import com.microsoft.azure.eventprocessorhost.IEventProcessorFactory;
import com.microsoft.azure.eventprocessorhost.PartitionContext;

public class NotificationEventProcessorFactory implements IEventProcessorFactory {
    private INotification notification;

    @Inject
    public NotificationEventProcessorFactory(INotification notification){
        this.notification = notification;
    }

    @Override
    public IEventProcessor createEventProcessor(PartitionContext context) throws Exception {
        return new NotificationEventProcessor(this.notification);
    }
}
