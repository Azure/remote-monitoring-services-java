// Copyright (c) Microsoft. All rights reserved.

package services.test.notification.test;

import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.IImplementationWrapper;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.INotification;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.Notification;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.implementation.IImplementation;
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
    private IImplementationWrapper implementationWrapperMock;
    private IImplementation implementationMock;

    @Before
    public void setUp(){
        this.implementationWrapperMock = Mockito.mock(IImplementationWrapper.class);
        this.implementationMock = Mockito.mock(IImplementation.class);
    }

    @Test
    public void CallsExecuteMethodEqualToNumberofCallstoNumberOfActionItemsInAlert(int numActionItems, int numCalls){
        INotification notification = new Notification(this.implementationWrapperMock);
        notification.setAlarm(this.getSampleAlarmWithNActions(numActionItems));
        Mockito.when(notification.executeAsync()).thenReturn(CompletableFuture.completedFuture(true));

        notification.executeAsync();

        Mockito.verify(this.implementationMock, Mockito.times(numCalls)).execute();
        Mockito.verify(this.implementationMock, Mockito.times(numCalls)).setMessage(
                "This is a test email",
                "12345",
                "Sample test description"
        );

        List<String> emails = new ArrayList<>();
        emails.add("Email");

        Mockito.verify(this.implementationMock, Mockito.times(numCalls)).setReceiver(
                emails
        );
    }

    private AlarmNotificationAsaModel getSampleAlarmWithNActions(int n){
        List<ActionAsaModel> actionList = new ArrayList<>();
        for(int i = 0; i < n; i++){
            actionList.add(this.getSampleAction());
        }

        AlarmNotificationAsaModel model = new AlarmNotificationAsaModel();
        model.setActions(actionList);
        model.setRule_id("12345");
        model.setRule_description("Sample test description");

        return model;
    }

    private ActionAsaModel getSampleAction(){
        Map<String, Object> params = new HashMap<>();
        params.put("Template", "This is a test email");
        params.put("Subject", "Test subject");

        List<String> emails = new ArrayList<>();
        params.put("Email", emails);

        ActionAsaModel model = new ActionAsaModel();
        model.setActionType("Email");
        model.setParameters(params);
        return model;
    }
}
