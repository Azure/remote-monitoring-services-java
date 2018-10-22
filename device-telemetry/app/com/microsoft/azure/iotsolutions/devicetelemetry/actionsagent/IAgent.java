// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent;

import java.util.concurrent.CompletionStage;

public interface IAgent {
    CompletionStage runAsync();
}
