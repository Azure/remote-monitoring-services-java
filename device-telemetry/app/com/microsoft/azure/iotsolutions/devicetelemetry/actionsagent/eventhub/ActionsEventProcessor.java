// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.eventhub;

import com.google.inject.Inject;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventprocessorhost.CloseReason;
import com.microsoft.azure.eventprocessorhost.IEventProcessor;
import com.microsoft.azure.eventprocessorhost.PartitionContext;
import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.actions.AlarmParser;
import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.models.AsaAlarmApiModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.actions.IActionManager;
import play.Logger;

import java.util.List;

public class ActionsEventProcessor implements IEventProcessor {

    private static final Logger.ALogger logger = Logger.of(ActionsEventProcessor.class);
    private IActionManager actionManager;

    @Inject
    public ActionsEventProcessor(IActionManager actionManager) {
        this.actionManager = actionManager;
    }

    @Override
    public void onOpen(PartitionContext context) {
        this.logger.info(String.format("ActionManager EventProcessor initialized. Partition: %s", context.getPartitionId()));
    }

    @Override
    public void onClose(PartitionContext context, CloseReason reason) throws Exception {
        this.logger.info(String.format("ActionManager EventProcessor shutting down. Partition %s, Reason: %s", context.getPartitionId(), reason.toString()));
        context.checkpoint().get();
    }

    @Override
    public void onEvents(PartitionContext context, Iterable<EventData> events) throws Exception {
        for (EventData eventData : events) {
            String data = new String(eventData.getBytes(), "UTF8");
            List<AsaAlarmApiModel> alarms = AlarmParser.parseAlarmList(data);
            actionManager.executeAsync(alarms).toCompletableFuture().get();
        }
        context.checkpoint().get();
    }

    @Override
    public void onError(PartitionContext context, Throwable error) {
        this.logger.info(String.format("Error on Partition: %s, Error: %s", context.getPartitionId(), error.getMessage()));
    }
}