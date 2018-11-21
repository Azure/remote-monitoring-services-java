// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.iothubmanager.services.IDeployments;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.auth.Authorize;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.ResourceNotFoundException;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models.DeploymentApiModel;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models.DeploymentListApiModel;
import org.apache.commons.lang3.StringUtils;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.concurrent.CompletionStage;

import static play.libs.Json.fromJson;
import static play.libs.Json.toJson;

public class DeploymentsController extends Controller {
    private final IDeployments deploymentsService;

    @Inject
    public DeploymentsController(final IDeployments deploymentsService) {
        this.deploymentsService = deploymentsService;
    }

    /**
     * Retrieves all deployments that have been scheduled on the iothub.
     * Only deployments which were created by RM will be returned.
     * @return {@link DeploymentListApiModel} which contains a list of each {@link DeploymentApiModel}
     * @throws ExternalDependencyException thrown if there is an issue querying the RegistryManager. Details
     *      * are provided in the inner exception.
     */
    @Authorize("ReadAll")
    public CompletionStage<Result> getDeploymentsAsync() throws ExternalDependencyException {
        return this.deploymentsService.listAsync()
                .thenApply(deployments -> ok(toJson(new DeploymentListApiModel(deployments))));
    }

    /**
     * Retrieves a single deployment based on its id.
     *
     * @return The {@link DeploymentApiModel} corresponding to the deployment id provided.
     * 404 - Http not found is returned if the id provided doesn't belong to a deployment.
     * 400 - Bad request is returned if an id is not provided
     * @throws ExternalDependencyException thrown if there is an issue querying the RegistryManager. Details
     * are provided in the inner exception.
     */
    @Authorize("ReadAll")
    public CompletionStage<Result> getDeployment(final String id, boolean includeDeviceStatus) throws
            ExternalDependencyException, InvalidInputException {
        if (StringUtils.isEmpty(id)) {
            throw new InvalidInputException("Must specify deployment id to retrieve");
        }

        return this.deploymentsService.getAsync(id, includeDeviceStatus)
                .thenApply(deployment -> ok(toJson(new DeploymentApiModel(deployment))));
    }

    /**
     * Deletes the deployment of the given id.
     *
     * @return True if the deployment was deleted successfully.
     * 404 - Http not found is returned if the id provided doesn't belong to a deployment.
     * 400 - Bad request is returned if an id is not provided
     * @throws ExternalDependencyException thrown if there is an issue querying the RegistryManager. Details
     * are provided in the inner exception.
     * @throws InvalidInputException thrown if a deployment id is not provided.
     * @throws ResourceNotFoundException thrown if a deployment with the given id is not found.
     */
    @Authorize("DeleteDeployments")
    public CompletionStage<Result> deleteAsync(final String id) throws
            ExternalDependencyException, InvalidInputException, ResourceNotFoundException {
        if (StringUtils.isEmpty(id)) {
            throw new InvalidInputException("Must specify the id of the deployment to delete");
        }

        return this.deploymentsService.deleteAsync(id).thenApply(result -> ok());
    }

    /**
     * Creates a new deployment with the values specified in the body as JSON in the form of
     * {@link DeploymentApiModel}.
     *
     * @return A {@link DeploymentApiModel} modeling the newly created configuration.
     * 400 - Bad request is returned if deviceGroupId, packageId, name, or priority are invalid.
     * @throws ExternalDependencyException thrown if there is an issue communicating with the hub or in the
     * creation of the deployment. Details are provided in the inner exception.
     */
    @Authorize("CreateDeployments")
    public CompletionStage<Result> postAsync() throws ExternalDependencyException, InvalidInputException {
        final JsonNode json = request().body().asJson();
        final DeploymentApiModel deployment = fromJson(json, DeploymentApiModel.class);

        if (StringUtils.isEmpty(deployment.getDeviceGroupId())) {
            throw new InvalidInputException("DeviceGroupId must be provided");
        }

        if (StringUtils.isEmpty(deployment.getDeviceGroupQuery())) {
            throw new InvalidInputException("DeviceGroupQuery must be provided");
        }

        if (StringUtils.isEmpty(deployment.getPackageContent())) {
            throw new InvalidInputException("PackageContent must be provided");
        }

        if (StringUtils.isEmpty(deployment.getName())) {
            throw new InvalidInputException("Name must be provided");
        }

        if (StringUtils.isEmpty(deployment.getConfigType())) {
            throw new InvalidInputException("Config Type must be provided");
        }

        if (deployment.getPriority() < 0) {
            throw new InvalidInputException("Priority must be greater than or equal to zero");
        }

        return deploymentsService.createAsync(deployment.toServiceModel())
                .thenApply(newDevice -> ok(toJson(new DeploymentApiModel(newDevice))));
    }
}
