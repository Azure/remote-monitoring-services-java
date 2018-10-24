// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.eventhub;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.microsoft.azure.eventprocessorhost.IEventProcessor;
import com.microsoft.azure.eventprocessorhost.IEventProcessorFactory;
import com.microsoft.azure.eventprocessorhost.PartitionContext;
import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.actions.IActionManager;

@Singleton
public class ActionsEventProcessorFactory implements IEventProcessorFactory {

    private IActionManager actionManager;

    @Inject
    public ActionsEventProcessorFactory(IActionManager actionManager) {
        this.actionManager = actionManager;
    }

    @Override
    public IEventProcessor createEventProcessor(PartitionContext context) {
        return new ActionsEventProcessor(this.actionManager);
    }
}