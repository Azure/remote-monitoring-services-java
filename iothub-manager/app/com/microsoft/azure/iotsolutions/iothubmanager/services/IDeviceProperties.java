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
    CompletionStage<TreeSet<String>> getListAsync();

    CompletionStage<DevicePropertyServiceModel> updateListAsync(DevicePropertyServiceModel devicePropertyServiceModel) throws BaseException, ExecutionException, InterruptedException;

    CompletionStage tryRecreateListAsync(boolean force) throws Exception;

    default CompletionStage tryRecreateListAsync() throws Exception {
        return tryRecreateListAsync(false);
    }
}
