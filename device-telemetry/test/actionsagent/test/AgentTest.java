// Copyright (c) Microsoft. All rights reserved.

package actionsagent.test;

import com.microsoft.azure.eventprocessorhost.IEventProcessorFactory;
import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.Agent;
import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.IAgent;
import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.eventhub.IEventProcessorHostWrapper;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.ActionsConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.ServicesConfig;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AgentTest {
    private IServicesConfig servicesConfigMock;
    private IEventProcessorHostWrapper eventProcessorHostWrapperMock;
    private IEventProcessorFactory eventProcessorFactoryMock;
    private IAgent agent;

    @Before
    public void setUp() throws ExecutionException, InterruptedException {
        this.eventProcessorHostWrapperMock = Mockito.mock(IEventProcessorHostWrapper.class);
        this.eventProcessorFactoryMock = Mockito.mock(IEventProcessorFactory.class);
        this.servicesConfigMock = new ServicesConfig(
            "",
            "",
            null,
            null,
            new ActionsConfig(
                "eventHubName",
                "eventHubConnectionString",
                0,
                "blobStorageConnectionString",
                "checkpointContainerName",
                "logicAppEndPointUrl",
                "solutionWebsiteUrl",
                "./data"
            ),
            null
        );
        this.agent = new Agent(
            this.servicesConfigMock,
            this.eventProcessorHostWrapperMock,
            this.eventProcessorFactoryMock);
    }

    @Test
    public void RegisterEventProcessorFactory() throws ExecutionException, InterruptedException {
        // Arrage
        Mockito.when(this.agent.runAsync()).thenReturn(CompletableFuture.completedFuture(true));

        // Act
        this.agent.runAsync().toCompletableFuture().get();

        // Assert
        Mockito.verify(this.eventProcessorHostWrapperMock, Mockito.times(2))
            .registerEventProcessorFactoryAsync(Mockito.any(), Mockito.any(IEventProcessorFactory.class));
    }
}
