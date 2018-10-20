package com.microsoft.azure.iotsolutions.devicetelemetry.RecurringTasksAgent;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.microsoft.azure.eventprocessorhost.IEventProcessorFactory;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.Agent;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.IAgent;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.eventhub.IEventProcessorHostWrapper;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServiceConfig;

import java.util.concurrent.CompletableFuture;

@Singleton
public class RecurringTasks implements IRecurringTasks {
    private IAgent notificationAgent;
    private IServiceConfig servicesConfig;
    private IEventProcessorHostWrapper eventProcessorHostWrapper;
    private IEventProcessorFactory notificationEventProcessorFactory;

    @Inject
    public RecurringTasks(
            IServiceConfig servicesConfig,
            IEventProcessorHostWrapper eventProcessorHostWrapper,
            IEventProcessorFactory notificationEventProcessorFactory )
    {
        this.servicesConfig = servicesConfig;
        this.eventProcessorHostWrapper = eventProcessorHostWrapper;
        this.notificationEventProcessorFactory = notificationEventProcessorFactory;
        CompletableFuture.runAsync(() -> this.run());
    }

    @Override
    public void run() {
        tryMethod();
    }

    private void tryMethod(){
        this.notificationAgent = new Agent(servicesConfig, eventProcessorHostWrapper, notificationEventProcessorFactory);
        notificationAgent.runAsync();
    }
}
