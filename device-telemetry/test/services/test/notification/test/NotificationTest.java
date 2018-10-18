// Copyright (c) Microsoft. All rights reserved.

package services.test.notification.test;

import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.INotificationImplementationWrapper;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.INotification;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.Notification;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.implementation.INotificationImplementation;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.models.ActionAsaModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.models.AlarmNotificationAsaModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import play.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class NotificationTest {
    private static final Logger.ALogger log = Logger.of(NotificationTest.class);
    private INotificationImplementationWrapper implementationWrapperMock;
    private INotificationImplementation implementationMock;

    @Before
    public void setUp(){
        this.implementationWrapperMock = Mockito.mock(INotificationImplementationWrapper.class);
        this.implementationMock = Mockito.mock(INotificationImplementation.class);
    }

    @Test
    public void CallsExecuteMethodEqualToNumberOfCallsToNumberOfActionItemsInAlert(){
        int numActionItems = 3;
        int numCalls = 3;
        INotification notification = new Notification(this.implementationWrapperMock);
        Mockito.when(this.implementationWrapperMock.getImplementationType(Mockito.any())).thenReturn(this.implementationMock);
        notification.setAlarm(this.getSampleAlarmWithNActions(numActionItems));
        Mockito.when(notification.executeAsync()).thenReturn(CompletableFuture.completedFuture(true));

        Mockito.verify(this.implementationMock, Mockito.times(numCalls)).setMessage(
                Mockito.any(String.class),
                Mockito.any(String.class),
                Mockito.any(String.class)
        );

        List<String> emails = new ArrayList<>();
        emails.add("test@gmail.com");

        Mockito.verify(this.implementationMock, Mockito.times(numCalls)).setReceiver(
                emails
        );
    }

    @Test
    public void ShouldNotCallExecuteWhenAlertHasNoActions(){
        Notification notification = new Notification(this.implementationWrapperMock);

        AlarmNotificationAsaModel alarm = new AlarmNotificationAsaModel();
        alarm.setActions(new ArrayList<>());
        notification.setAlarm(alarm);
        notification.executeAsync();

        Mockito.verify(this.implementationMock, Mockito.never()).execute();
        Mockito.verify(this.implementationMock, Mockito.never()).setMessage(
                Mockito.any(String.class),
                Mockito.any(String.class),
                Mockito.any(String.class)
        );

        Mockito.verify(this.implementationMock, Mockito.never()).setReceiver(
                Mockito.any(ArrayList.class)
        );
    }


    private AlarmNotificationAsaModel getSampleAlarmWithNActions(int n){
        List<ActionAsaModel> actionList = new ArrayList<>();
        for(int i = 0; i < n; i++){
            actionList.add(this.getSampleAction());
        }

        AlarmNotificationAsaModel model = new AlarmNotificationAsaModel();
        model.setActions(actionList);
        model.setRuleId("12345");
        model.setRuleDescription("Sample Description");

        return model;
    }

    private ActionAsaModel getSampleAction(){
        Map<String, Object> params = new HashMap<>();
        params.put("Template", "This is a test email");
        params.put("Subject", "Test subject");

        List<String> emails = new ArrayList<>();
        emails.add("test@gmail.com");
        params.put("Email", emails);

        ActionAsaModel model = new ActionAsaModel();
        model.setActionType("Email");
        model.setParameters(params);
        return model;
    }
}
