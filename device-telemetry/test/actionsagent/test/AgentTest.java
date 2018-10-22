// Copyright (c) Microsoft. All rights reserved.

package actionsagent.test;

import com.microsoft.azure.eventprocessorhost.IEventProcessorFactory;
import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.Agent;
import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.IAgent;
import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.eventhub.IEventProcessorHostWrapper;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.ActionsConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServiceConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.ServicesConfig;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.CompletableFuture;

public class AgentTest {
    private IServiceConfig servicesConfigMock;
    private IEventProcessorHostWrapper eventProcessorHostWrapperMock;
    private IEventProcessorFactory eventProcessorFactoryMock;
    private IAgent notificationSystemAgent;

    @Before
    public void setUp() {
        this.servicesConfigMock = Mockito.mock(ServicesConfig.class);
        this.eventProcessorHostWrapperMock = Mockito.mock(IEventProcessorHostWrapper.class);
        this.eventProcessorFactoryMock = Mockito.mock(IEventProcessorFactory.class);

        this.notificationSystemAgent = new Agent(
            this.servicesConfigMock,
            this.eventProcessorHostWrapperMock,
            this.eventProcessorFactoryMock);
    }

    @Test
    public void RegisterEventProcessorFactory() {
        Mockito.when(this.servicesConfigMock.getActionsConfig()).thenReturn(new ActionsConfig(
            "eventHubName",
            "eventHubConnectionString",
            0,
            "blobStorageConnectionString",
            "checkpointContainerName",
            "logicAppEndPointUrl",
            "solutionWebsiteUrl",
            "./data"
        ));
        Mockito.when(this.notificationSystemAgent.runAsync()).thenReturn(CompletableFuture.completedFuture(true));
        this.notificationSystemAgent.runAsync();
        Mockito.verify(this.eventProcessorHostWrapperMock, Mockito.times(1)).registerEventProcessorFactoryAsync(
            Mockito.any(),
            Mockito.any(IEventProcessorFactory.class));
    }
}
