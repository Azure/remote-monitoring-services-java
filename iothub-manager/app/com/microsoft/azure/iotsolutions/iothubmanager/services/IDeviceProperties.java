// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.*;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DevicePropertyServiceModel;

import java.util.TreeSet;
import java.util.concurrent.CompletionStage;

@ImplementedBy(DeviceProperties.class)
public interface IDeviceProperties {

    CompletionStage<TreeSet<String>> getListAsync() throws
        ResourceNotFoundException,
        ConflictingResourceException,
        ExternalDependencyException,
        InvalidInputException;

    CompletionStage<DevicePropertyServiceModel> updateListAsync(
        DevicePropertyServiceModel devicePropertyServiceModel)
        throws InterruptedException, ExternalDependencyException;

    CompletionStage tryRecreateListAsync(boolean force)
        throws InterruptedException, ExternalDependencyException;

    default CompletionStage tryRecreateListAsync()
        throws InterruptedException, ExternalDependencyException {
        return tryRecreateListAsync(false);
    }
}
