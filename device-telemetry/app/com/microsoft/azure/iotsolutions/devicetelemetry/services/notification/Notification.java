package com.microsoft.azure.iotsolutions.devicetelemetry.services.notification;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.implementation.LogicApp;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.models.ActionAsaModel;
import play.libs.ws.WSClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;

public class Notification {
    public List<ActionAsaModel> actionList;
    public INotification implementation;

    private String ruleId;
    private String ruleDescription;
    private WSClient wsClient;

    @Inject
    public Notification(WSClient wsClient) {
        this.wsClient = wsClient;
    }

    public void setActionList(List<ActionAsaModel> actionList) {
        this.actionList = actionList;
    }

    public void setAlarmInformation(String ruleId, String ruleDescription) {
        this.ruleId = ruleId;
        this.ruleDescription = ruleDescription;
    }

    public CompletionStage executeAsync() {
        try {
            for(ActionAsaModel action : this.actionList){
                if(action.getActionType().equals("Email")){
                    implementation = new LogicApp(wsClient);
                    Map<String, String> credentialMap = new HashMap<String, String>();
                    credentialMap.put("endPointURL", "https://prod-00.southeastasia.logic.azure.com:443/workflows/1f2493004aea43e1ac661f071a15f330/triggers/manual/paths/invoke?api-version=2016-10-01&sp=%2Ftriggers%2Fmanual%2Frun&sv=1.0&sig=DIfPL17M7qydXwHxD7g-_K-P3mE6dqYuv7aDfbQji94");
                    implementation.setCredentials(credentialMap);
                }
                implementation.setMessage((String) action.getParameters().get("Template"), this.ruleId, this.ruleDescription);
                implementation.setReceiver(((ArrayList<String>) action.getParameters().get("Email")).get(0));
                implementation.execute(); // how to make it await
            }
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            throw new CompletionException(e);
        }
    }
}
