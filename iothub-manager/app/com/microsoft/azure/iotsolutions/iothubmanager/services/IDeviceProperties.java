// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.BaseException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DevicePropertyServiceModel;

import java.util.TreeSet;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

@ImplementedBy(DeviceProperties.class)
public interface IDeviceProperties {
    CompletionStage<TreeSet<String>> GetListAsync();

    CompletionStage<DevicePropertyServiceModel> UpdateListAsync(DevicePropertyServiceModel devicePropertyServiceModel) throws BaseException, ExecutionException, InterruptedException;

    CompletionStage TryRecreateListAsync(boolean force) throws Exception;

    default CompletionStage TryRecreateListAsync() throws Exception {
        return TryRecreateListAsync(false);
    }
}
