// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent;

import java.util.concurrent.CompletionStage;

/**
 * Agent running for ever in the background
 */
public interface IAgent {
    CompletionStage runAsync();
}
