// Copyright (c) Microsoft. All rights reserved.

package actionsagent.test;

import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.actions.ActionManager;
import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.models.AsaAlarmApiModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.*;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.EmailActionServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.IActionServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.ActionsConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.ServicesConfig;
import org.junit.Before;
import org.junit.Test;
import play.libs.ws.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.*;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ActionManagerTest {

    private ActionManager actionManager;
    private WSClient mockClient;
    private AsaAlarmApiModel alarm;

    @Before
    public void setUp() throws ResourceNotFoundException, InvalidInputException {
        ServicesConfig servicesConfig = new ServicesConfig(
            "keyValueStorageUrl",
            "userManagementApiUrl",
            null,
            null,
            new ActionsConfig(
                "",
                "",
                10,
                "",
                "",
                "https://azure.com",
                "test",
                "data"
            ),
            null
        );

        this.mockClient = mock(WSClient.class);
        this.actionManager = new ActionManager(servicesConfig, this.mockClient);
        IActionServiceModel action = new EmailActionServiceModel(
            new HashMap<String, Object>() {{
                put("Recipients", Arrays.asList("sampleEmail@gmail.com"));
                put("Notes", "Test Note");
                put("Subject", "Test Subject");
            }}
        );
        this.alarm = new AsaAlarmApiModel();
        alarm.setDateCreated("1539035437937");
        alarm.setDeviceId("Test Device Id");
        alarm.setMessageReceived("1539035437937");
        alarm.setRuleDescription("Test Rule description");
        alarm.setRuleId("TestRuleId");
        alarm.setRuleSeverity("Warning");
        alarm.setActions(Arrays.asList(action));
    }

    @Test
    public void EmailAction_CausesPostToLogicApp() {
        // Arrange
        WSRequest mockRequest = spy(WSRequest.class);
        WSResponse mockResponse = spy(WSResponse.class);
        CompletableFuture<WSResponse> future = spy(CompletableFuture.supplyAsync(() -> mockResponse));

        doReturn(202).when(mockResponse).getStatus();
        doReturn(mockRequest).when(mockClient).url(anyString());
        doReturn(mockRequest).when(mockRequest).addHeader(anyString(), anyString());
        doReturn(future).when(mockRequest).post(anyString());
        doReturn(CompletableFuture.supplyAsync(() -> true)).when(future).handle(any());

        // Act
        CompletionStage result = this.actionManager.executeAsync(Arrays.asList(this.alarm));
    }

    @Test(expected = CompletionException.class)
    public void EmailAction_ThrowExceptionWhenPostToLogicApp() {
        // Arrange
        WSRequest mockRequest = spy(WSRequest.class);
        WSResponse mockResponse = spy(WSResponse.class);
        CompletableFuture<WSResponse> future = spy(CompletableFuture.supplyAsync(() -> mockResponse));

        doReturn(202).when(mockResponse).getStatus();
        doReturn(mockRequest).when(mockClient).url(anyString());
        doReturn(mockRequest).when(mockRequest).addHeader(anyString(), anyString());
        doReturn(future).when(mockRequest).post(anyString());
        doThrow(new CompletionException(new ExternalDependencyException("Could not connect to logic app"))).when(future).handle(any());

        // Act
        CompletionStage result = this.actionManager.executeAsync(Arrays.asList(alarm));
    }
}
