// Copyright (c) Microsoft. All rights reserved.

package services.test.notification.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventprocessorhost.IEventProcessor;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.INotification;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.Notification;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.NotificationEventProcessor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class NotificationEventProcessorTest {
    private INotification notificationMock;
    private IEventProcessor notificationEventProcessor;

    @Before
    public void setUp() {
        this.notificationMock = Mockito.mock(Notification.class);
        this.notificationEventProcessor = new NotificationEventProcessor(this.notificationMock);
    }

    @Test
    public void ShouldCallExecuteEqualToNumberOfJsonTokensInEventData() throws Exception {
        Mockito.when(notificationMock.executeAsync()).thenReturn(CompletableFuture.completedFuture(true));

        Collection<EventData> list = new ArrayList<>();
        EventData data = EventData.create(this.getSamplePayLoadData());
        list.add(data);
        this.notificationEventProcessor.onEvents(null, list);
        Mockito.verify(this.notificationMock, Mockito.times(1)).executeAsync();
    }

    @Test
    public void ShouldNotCallExecuteWhenEmptyEventData() throws Exception {
        Mockito.when(notificationMock.executeAsync()).thenReturn(CompletableFuture.completedFuture(true));
        this.notificationEventProcessor.onEvents(null, new ArrayList<>());
        Mockito.verify(this.notificationMock, Mockito.never()).executeAsync();
    }

    private byte[] getSamplePayLoadData() throws JsonProcessingException {
        byte[] byteArray = new ObjectMapper().writeValueAsBytes(this.getMap());
        return byteArray;
    }

    private Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("created", "342874237482374");
        map.put("modified", "1234123123123");
        map.put("rule.description", "Pressure > 380");
        map.put("rule.severity", "Critical");
        map.put("rule.id", "12345");

        List<Object> actionList = new ArrayList<>();
        Map<String, Object> actions = new HashMap<>();
        actions.put("Type", "Email");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("Template", "This is a test email.");

        List<String> emails = new ArrayList<>();
        emails.add("sample@gamil.com");
        emails.add("sample@microsoft.com");
        parameters.put("Email", emails);

        actions.put("Parameters", parameters);
        actionList.add(actions);
        map.put("rule.actions", actionList);
        map.put("device.id", "213123");
        map.put("device.msg.received", "1234123123123");

        return map;
    }
}
