// Copyright (c) Microsoft. All rights reserved.

package actionsagent.test;

import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.actions.ActionManager;
import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.actions.IActionManager;
import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.actions.IActionExecutor;
import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.models.AsaAlarmsApiModel;
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
//    private INotificationImplementationWrapper implementationWrapperMock;
//    private IActionExecutor implementationMock;

    @Before
    public void setUp(){
//        this.implementationWrapperMock = Mockito.mock(INotificationImplementationWrapper.class);
//        this.implementationMock = Mockito.mock(IActionExecutor.class);
    }

//    @Test
//    public void CallsExecuteMethodEqualToNumberOfCallsToNumberOfActionItemsInAlert(){
//        int numActionItems = 3;
//        int numCalls = 3;
//        IActionManager notification = new ActionManager(this.implementationWrapperMock);
//        Mockito.when(this.implementationWrapperMock.getImplementationType(Mockito.any())).thenReturn(this.implementationMock);
//        notification.setAlarm(this.getSampleAlarmWithNActions(numActionItems));
//        Mockito.when(notification.executeAsync()).thenReturn(CompletableFuture.completedFuture(true));
//
//        Mockito.verify(this.implementationMock, Mockito.times(numCalls)).setMessage(
//                Mockito.any(String.class),
//                Mockito.any(String.class),
//                Mockito.any(String.class)
//        );
//
//        List<String> emails = new ArrayList<>();
//        emails.add("test@gmail.com");
//
//        Mockito.verify(this.implementationMock, Mockito.times(numCalls)).setReceiver(
//                emails
//        );
//    }
//
//    @Test
//    public void ShouldNotCallExecuteWhenAlertHasNoActions(){
//        ActionManager notification = new ActionManager(this.implementationWrapperMock);
//
//        AsaAlarmsApiModel alarm = new AsaAlarmsApiModel();
//        alarm.setActions(new ArrayList<>());
//        notification.setAlarm(alarm);
//        notification.executeAsync();
//
//        Mockito.verify(this.implementationMock, Mockito.never()).execute();
//        Mockito.verify(this.implementationMock, Mockito.never()).setMessage(
//                Mockito.any(String.class),
//                Mockito.any(String.class),
//                Mockito.any(String.class)
//        );
//
//        Mockito.verify(this.implementationMock, Mockito.never()).setReceiver(
//                Mockito.any(ArrayList.class)
//        );
//    }
//
//
//    private AsaAlarmsApiModel getSampleAlarmWithNActions(int n){
//        List<ActionAsaModel> actionList = new ArrayList<>();
//        for(int i = 0; i < n; i++){
//            actionList.add(this.getSampleAction());
//        }
//
//        AsaAlarmsApiModel model = new AsaAlarmsApiModel();
//        model.setActions(actionList);
//        model.setRuleId("12345");
//        model.setRuleDescription("Sample Description");
//
//        return model;
//    }
//
//    private ActionAsaModel getSampleAction(){
//        Map<String, Object> params = new HashMap<>();
//        params.put("Template", "This is a test email");
//        params.put("Subject", "Test subject");
//
//        List<String> emails = new ArrayList<>();
//        emails.add("test@gmail.com");
//        params.put("Email", emails);
//
//        ActionAsaModel model = new ActionAsaModel();
//        model.setActionType("Email");
//        model.setParameters(params);
//        return model;
//    }
}
