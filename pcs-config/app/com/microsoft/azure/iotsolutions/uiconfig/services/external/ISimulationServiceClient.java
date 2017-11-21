// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external;

import com.google.inject.ImplementedBy;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.concurrent.CompletionStage;

@ImplementedBy(SimulationServiceClient.class)
public interface ISimulationServiceClient {
    CompletionStage<HashSet<String>> getDevicePropertyNamesAsync() throws URISyntaxException;
}
