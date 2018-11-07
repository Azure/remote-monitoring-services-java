// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.StatusServiceModel;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

@ImplementedBy(StatusService.class)
public interface IStatusService {
    CompletionStage<StatusServiceModel> GetStatusAsync(boolean authRequired) throws ExecutionException, InterruptedException;
}
