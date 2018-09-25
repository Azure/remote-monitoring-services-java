// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.external;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.ResourceNotFoundException;

import java.util.concurrent.CompletionStage;

@ImplementedBy(DeviceGroupsClient.class)
public interface IDeviceGroupsClient {
    CompletionStage<DeviceGroupApiModel> getDeviceGroupAsync(String deviceGroupId) throws ResourceNotFoundException,
            ExternalDependencyException;
}
