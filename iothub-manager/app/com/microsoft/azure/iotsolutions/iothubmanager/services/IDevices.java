// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.*;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceServiceModel;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceServiceListModel;

import java.io.IOException;
import java.util.concurrent.CompletionStage;

// TODO: documentation

@ImplementedBy(Devices.class)
public interface IDevices {

    CompletionStage<DeviceServiceListModel> queryAsync(String query, String continuationToken) throws BaseException, IOException, IotHubException;

    CompletionStage<DeviceServiceModel> getAsync(String id) throws IOException, IotHubException;

    CompletionStage<DeviceServiceModel> createAsync(DeviceServiceModel device) throws BaseException, IOException, IotHubException;

    CompletionStage<DeviceServiceModel> createOrUpdateAsync(String id, DeviceServiceModel device) throws BaseException, IOException, IotHubException;

    CompletionStage<Boolean> deleteAsync(String id) throws IOException, IotHubException;
}
