// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceServiceModel;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

// TODO: documentation

@ImplementedBy(Devices.class)
public interface IDevices {

    CompletionStage<ArrayList<DeviceServiceModel>> getListAsync() throws IOException, IotHubException;

    CompletionStage<DeviceServiceModel> getAsync(String id) throws IOException, IotHubException;

    CompletionStage<DeviceServiceModel> createAsync(DeviceServiceModel device) throws InvalidInputException, IOException, IotHubException;

    CompletionStage<Boolean> deleteAsync(String id) throws IOException, IotHubException;
}
