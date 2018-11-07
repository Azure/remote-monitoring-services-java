// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.storageadapter.services.models.StatusServiceModel;

import java.util.concurrent.CompletionStage;

@ImplementedBy(StatusService.class)
public interface IStatusService {
    CompletionStage<StatusServiceModel> GetStatusAsync();
}
