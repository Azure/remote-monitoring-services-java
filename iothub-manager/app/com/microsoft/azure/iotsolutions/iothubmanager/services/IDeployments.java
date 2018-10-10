// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.ResourceNotFoundException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeploymentServiceListModel;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeploymentServiceModel;

import java.util.concurrent.CompletionStage;

@ImplementedBy(Deployments.class)
public interface IDeployments {

    /**
     * Retrieves all deployments that have been scheduled on the iothub.
     * Only deployments which were created by RM will be returned.
     * @return {@link DeploymentServiceListModel} of {@link DeploymentServiceModel}
     * @throws ExternalDependencyException thrown if there is an issue querying the RegistryManager. Details
     * are provided in the inner exception.
     */
    CompletionStage<DeploymentServiceListModel> listAsync() throws ExternalDependencyException;

    /**
     * Retrieves a single deployment based on its id.
     *
     * @return The {@link DeploymentServiceModel} corresponding to the deployment id provided.
     * @throws ResourceNotFoundException - Signifying an id that isn't found. Wrapped in a completionException
     * @throws ExternalDependencyException thrown if there is an issue querying the RegistryManager. Details
     * are provided in the inner exception.
     */
    CompletionStage<DeploymentServiceModel> getAsync(String id, boolean includeDeviceStatus) throws ExternalDependencyException;

    /**
     * Schedules a new deployment with the content provided by the package id targeting the group in
     * deviceGroupId.
     * @param deployment - {@link DeploymentServiceModel} which has packageId, name, deviceGroupId, and type.
     * @return {@link DeploymentServiceModel} with the provided values as well as date created and
     * deployment id.
     * @throws InvalidInputException - If any of the required parameters (name, deviceGroupId, packageId,
     * or priority) are empty / invalid.
     * @throws ExternalDependencyException thrown if there is an issue querying the RegistryManager. Details
     * are provided in the inner exception.
     */
    CompletionStage<DeploymentServiceModel> createAsync(DeploymentServiceModel deployment)
            throws InvalidInputException, ExternalDependencyException;

    /**
     * Deletes the deployment of the given id.
     * @param id - Id of the deployment to be deleted
     * @return - true if the deployment was successfully deleted
     * @throws ResourceNotFoundException - Signifying an id that isn't found. Wrapped in a completionException
     * @throws ExternalDependencyException thrown if there is an issue querying the RegistryManager. Details
     * are provided in the inner exception.
     */
    CompletionStage<Boolean> deleteAsync(String id) throws ExternalDependencyException, ResourceNotFoundException;
}
