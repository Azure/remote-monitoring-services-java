// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceServiceModel;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

// TODO: documentation

@ImplementedBy(Devices.class)
public interface IDevices {

    CompletableFuture<ArrayList<DeviceServiceModel>> getListAsync();

    CompletableFuture<DeviceServiceModel> getAsync(String id);

    CompletableFuture<DeviceServiceModel> createAsync(DeviceServiceModel device);
}
