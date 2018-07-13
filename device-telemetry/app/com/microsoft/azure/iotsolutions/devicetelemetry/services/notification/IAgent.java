package com.microsoft.azure.iotsolutions.devicetelemetry.services.notification;

import java.util.concurrent.CompletionStage;

public interface IAgent {
    public CompletionStage runAsync();
}
