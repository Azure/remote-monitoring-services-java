// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent;

import com.google.inject.ImplementedBy;

import java.util.concurrent.CompletionStage;

@ImplementedBy(Agent.class)
public interface IAgent {
    CompletionStage runAsync();
}
