// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.ExternalDependencyException;

import java.util.concurrent.CompletionStage;

@ImplementedBy(DeviceSimulationClient.class)
public interface IDeviceSimulationClient {
    CompletionStage<SimulationApiModel> GetSimulationAsync() throws ExternalDependencyException;

    CompletionStage UpdateSimulationAsync(SimulationApiModel model) throws ExternalDependencyException;
}
