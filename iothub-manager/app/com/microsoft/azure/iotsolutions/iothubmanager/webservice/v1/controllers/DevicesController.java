// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.iothubmanager.services.DevicePropertyCallBack;
import com.microsoft.azure.iotsolutions.iothubmanager.services.IDeviceProperties;
import com.microsoft.azure.iotsolutions.iothubmanager.services.IDevices;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.auth.Authorize;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models.DeviceListApiModel;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models.DeviceRegistryApiModel;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models.MethodParameterApiModel;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models.MethodResultApiModel;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import java.util.concurrent.CompletionStage;

import static play.libs.Json.fromJson;
import static play.libs.Json.toJson;

public final class DevicesController extends Controller {

    private final IDevices deviceService;
    private final IDeviceProperties deviceProperties;
    final String ContinuationTokenName = "x-ms-continuation";

    @Inject
    public DevicesController(final IDevices deviceService, final IDeviceProperties deviceProperties) {
        this.deviceService = deviceService;
        this.deviceProperties = deviceProperties;
    }

    public CompletionStage<Result> getDevicesAsync(String query) throws ExternalDependencyException {
        String continuationToken = "";
        if (request().getHeaders().contains(ContinuationTokenName)) {
            continuationToken = request().getHeaders().getAll(ContinuationTokenName).get(0);
        }
        return deviceService.queryAsync(query, continuationToken)
            .thenApply(devices -> ok(toJson(new DeviceListApiModel(devices))));
    }

    public CompletionStage<Result> queryDevicesAsync() throws ExternalDependencyException {
        String continuationToken = "";
        String query;
        if (request().getHeaders().get(CONTENT_TYPE).isPresent() &&
            request().getHeaders().get(CONTENT_TYPE).get().equals(Http.MimeTypes.JSON)) {
            query = request().body().asJson().asText();
        } else {
            query = request().body().asText();
        }

        if (request().getHeaders().contains(ContinuationTokenName)) {
            continuationToken = request().getHeaders().getAll(ContinuationTokenName).get(0);
        }

        return deviceService.queryAsync(query, continuationToken)
            .thenApply(devices -> ok(toJson(new DeviceListApiModel(devices))));
    }

    public CompletionStage<Result> getDeviceAsync(final String id) throws ExternalDependencyException {
        return deviceService.getAsync(id)
            .thenApply(device -> ok(toJson(new DeviceRegistryApiModel(device))));
    }

    @Authorize("CreateDevices")
    public CompletionStage<Result> postAsync() throws InvalidInputException, ExternalDependencyException {
        JsonNode json = request().body().asJson();
        final DeviceRegistryApiModel device = fromJson(json, DeviceRegistryApiModel.class);
        return deviceService.createAsync(device.toServiceModel())
            .thenApply(newDevice -> ok(toJson(new DeviceRegistryApiModel(newDevice))));
    }

    @Authorize("UpdateDevices")
    public CompletionStage<Result> putAsync(final String id) throws InvalidInputException, ExternalDependencyException {
        JsonNode json = request().body().asJson();
        final DeviceRegistryApiModel device = fromJson(json, DeviceRegistryApiModel.class);
        IDeviceProperties deviceProperties = this.deviceProperties;
        DevicePropertyCallBack devicePropertyCallBack = devices -> {
            return deviceProperties.updateListAsync(devices);
        };
        return deviceService.createOrUpdateAsync(id, device.toServiceModel(), devicePropertyCallBack)
            .thenApply(newDevice -> ok(toJson(new DeviceRegistryApiModel(newDevice))));
    }

    @Authorize("DeleteDevices")
    public CompletionStage<Result> deleteAsync(final String id) throws ExternalDependencyException {
        return deviceService.deleteAsync(id)
            .thenApply(result -> ok());
    }

    @Authorize("CreateJobs")
    public CompletionStage<Result> invokeDeviceMethodAsync(final String id) throws ExternalDependencyException {
        JsonNode json = request().body().asJson();
        final MethodParameterApiModel parameter = fromJson(json, MethodParameterApiModel.class);
        return deviceService.invokeDeviceMethodAsync(id, parameter.toServiceModel())
            .thenApply(result -> ok(toJson(new MethodResultApiModel(result))));
    }
}
