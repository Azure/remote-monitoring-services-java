package com.microsoft.azure.iotsolutions.devicetelemetry.services.notification;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

public interface INotification {
    public enum EmailImplementationTypes{
        LogicApp
    }
    public Boolean setReceiver(List<String> receiver);
    public Boolean setMessage(String message, String ruleId, String ruleDescription);
    public Boolean setCredentials(Map<String, String> credentials);
    public CompletionStage execute();
}
