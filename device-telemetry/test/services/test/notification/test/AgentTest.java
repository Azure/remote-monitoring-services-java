// Copyright (c) Microsoft. All rights reserved.

package services.test.notification.test;

import com.microsoft.azure.eventprocessorhost.IEventProcessorFactory;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.Agent;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.IAgent;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.IEventProcessorHostWrapper;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import play.Logger;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;

public class AgentTest {
    private static final Logger.ALogger log = Logger.of(AgentTest.class);
    private IServicesConfig servicesConfig;
    private IEventProcessorHostWrapper eventProcessorHostWrapperMock;
    private IBlobStorageConfig blobStorageConfigMock;
    private IEventProcessorFactory eventProcessorFactoryMock;
    private IAgent notificationSystemAgent;

    @Before
    public void setUp(){
        ServicesConfig servicesConfig = new ServicesConfig(
                "storageConnection",
                "storageUrl",
                new StorageConfig(
                        "documentdb",
                        "connString",
                        "database",
                        "collection"),
                new AlarmsConfig(
                        "documentdb",
                        "connString",
                        "database",
                        "collection",
                        3
                ),
                "eventHubName",
                "eventHubConnectionString",
                0,
                "logicAppEndPointUrl",
                "solutionName");
        this.eventProcessorHostWrapperMock = Mockito.mock(IEventProcessorHostWrapper.class);
        this.blobStorageConfigMock = Mockito.mock(IBlobStorageConfig.class);
        this.eventProcessorFactoryMock = Mockito.mock(IEventProcessorFactory.class);

        this.notificationSystemAgent = new Agent(
                this.servicesConfig,
                this.blobStorageConfigMock,
                this.eventProcessorHostWrapperMock,
                this.eventProcessorFactoryMock);
    }

    @Test
    public void RegisterEventProcessorFactory(){
        Mockito.when(this.notificationSystemAgent.runAsync()).thenReturn(CompletableFuture.completedFuture(true));
        assertEquals(this.notificationSystemAgent.runAsync(), CompletableFuture.completedFuture(true));
        Mockito.verify(this.notificationSystemAgent.runAsync());
    }
}
