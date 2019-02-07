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

    DeviceTwinName getDeviceTwinNames()
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

    /**
     * Retrieve the twin information for a module on a specific device. Performs the same operation as
     * calling {@link #getModuleTwinsByQueryAsync(String, String)} with a query like
     * "deviceId='dvcId' AND moduleId='modId'"
     *
     * @param deviceId - DeviceId which has the module to be found
     * @param moduleId - Id of module to retrieve.
     * @return {@link TwinServiceModel} with the module's twin information.
     * @throws InvalidInputException - Empty deviceId or moduleId (400 bad request).
     * @throws ResourceNotFoundException - Device or module not found. Wrapped in a completionException
     * (404 status code)
     * @throws ExternalDependencyException - Error communicating with the hub. (500 status code).
     */
    CompletionStage<TwinServiceModel> getModuleTwinAsync(String deviceId, String moduleId) throws
            ExternalDependencyException, InvalidInputException;

    /**
     * Search for module twin information using a query in a paginated fashion using continuation tokens.
     * If there are no more results the continuation token will be empty.
     *
     * @param query - Query to select list of module twins to return.
     * @param continuationToken - Token needed to paginate through list of results.
     * @return {@link TwinServiceListModel} with the list of module twin information.
     * @throws ExternalDependencyException - Error communicating with the hub. (500 status code).
     */
    CompletionStage<TwinServiceListModel> getModuleTwinsByQueryAsync(String query, String continuationToken)
            throws ExternalDependencyException;
}
