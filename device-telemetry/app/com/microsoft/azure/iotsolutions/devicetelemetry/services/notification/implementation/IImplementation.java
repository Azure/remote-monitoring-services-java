package com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.implementation;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface IImplementation {
    public void setReceiver(List<String> receivers);

    public void setMessage(String message, String ruleId, String ruleDescription);

    public CompletionStage execute();
}
