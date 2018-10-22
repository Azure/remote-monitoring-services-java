// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.eventhub;

import com.google.inject.Inject;
import com.microsoft.azure.eventprocessorhost.IEventProcessor;
import com.microsoft.azure.eventprocessorhost.IEventProcessorFactory;
import com.microsoft.azure.eventprocessorhost.PartitionContext;
import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.INotification;

public class ActionsEventProcessorFactory implements IEventProcessorFactory {
    private INotification notification;

    @Inject
    public ActionsEventProcessorFactory(INotification notification) {
        this.notification = notification;
    }

    @Override
    public IEventProcessor createEventProcessor(PartitionContext context) throws Exception {
        return new ActionsEventProcessor(this.notification);
    }
}