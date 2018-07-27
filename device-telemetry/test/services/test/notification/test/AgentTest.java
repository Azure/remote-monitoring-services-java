// Copyright (c) Microsoft. All rights reserved.

package services.test.notification.test;

import com.microsoft.azure.eventprocessorhost.EventProcessorOptions;
import com.microsoft.azure.eventprocessorhost.IEventProcessorFactory;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.Agent;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.IAgent;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.IEventProcessorHostWrapper;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IBlobStorageConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.ServicesConfig;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.CompletableFuture;

public class AgentTest {
    private IServicesConfig servicesConfigMock;
    private IEventProcessorHostWrapper eventProcessorHostWrapperMock;
    private IBlobStorageConfig blobStorageConfigMock;
    private IEventProcessorFactory eventProcessorFactoryMock;
    private IAgent notificationSystemAgent;

    @Before
    public void setUp() {
        this.servicesConfigMock = Mockito.mock(ServicesConfig.class);
        this.eventProcessorHostWrapperMock = Mockito.mock(IEventProcessorHostWrapper.class);
        this.blobStorageConfigMock = Mockito.mock(IBlobStorageConfig.class);
        this.eventProcessorFactoryMock = Mockito.mock(IEventProcessorFactory.class);

        this.notificationSystemAgent = new Agent(
                this.servicesConfigMock,
                this.blobStorageConfigMock,
                this.eventProcessorHostWrapperMock,
                this.eventProcessorFactoryMock);
    }

    @Test
    public void RegisterEventProcessorFactory() {
        Mockito.when(this.notificationSystemAgent.runAsync()).thenReturn(CompletableFuture.completedFuture(true));
        this.notificationSystemAgent.runAsync();
        Mockito.verify(this.eventProcessorHostWrapperMock, Mockito.times(1)).registerEventProcessorFactoryAsync(
                Mockito.any(),
                Mockito.any(IEventProcessorFactory.class),
                Mockito.any(EventProcessorOptions.class));
    }
}
