// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.actions;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface IActionExecutor {
    void setReceiver(List<String> receivers);

    void setMessage(String message, String ruleId, String ruleDescription);

    CompletionStage execute();
}
