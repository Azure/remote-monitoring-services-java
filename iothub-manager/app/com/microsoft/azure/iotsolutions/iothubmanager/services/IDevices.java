// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.*;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.*;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

// TODO: documentation

@ImplementedBy(Devices.class)
public interface IDevices {

    CompletionStage<DeviceServiceListModel> queryAsync(String query, String continuationToken)
        throws ExternalDependencyException;

    DevicePropertyServiceModel getDeviceProperties()
        throws ExternalDependencyException, ExecutionException, InterruptedException;

    CompletionStage<DeviceServiceModel> getAsync(String id) throws ExternalDependencyException;

    CompletionStage<DeviceServiceModel> createAsync(DeviceServiceModel device)
        throws InvalidInputException, ExternalDependencyException;

    CompletionStage<DeviceServiceModel> createOrUpdateAsync(
        String id, DeviceServiceModel device, DevicePropertyCallBack devicePropertyCallBack)
        throws InvalidInputException, ExternalDependencyException;

    CompletionStage<Boolean> deleteAsync(String id) throws ExternalDependencyException;

    CompletionStage<MethodResultServiceModel> invokeDeviceMethodAsync(
        String id, MethodParameterServiceModel parameter) throws ExternalDependencyException;
}
