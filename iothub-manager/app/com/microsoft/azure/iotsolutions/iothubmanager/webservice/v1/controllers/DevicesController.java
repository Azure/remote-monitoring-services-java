// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.microsoft.azure.iotsolutions.iothubmanager.services.IDevices;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.ResourceNotFoundException;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models.DeviceRegistryApiModel;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models.DeviceListApiModel;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.IOException;
import java.util.concurrent.*;

import static play.libs.Json.toJson;

@Singleton
public final class DevicesController extends Controller {

    private final IDevices devices;

    @Inject
    public DevicesController(final IDevices devices) {
        this.devices = devices;
    }

    public CompletionStage<Result> list() throws IOException, IotHubException {
        return devices.getListAsync()
            .thenApply(list -> ok(toJson(new DeviceListApiModel(list))));
    }

    public CompletionStage<Result> get(final String id) throws IOException, IotHubException {
        return devices.getAsync(id)
            .thenApply(device -> ok(toJson(new DeviceRegistryApiModel(device))));
    }

    public CompletionStage<Result> post() throws InvalidInputException, IOException, IotHubException {
        JsonNode json = request().body().asJson();
        final DeviceRegistryApiModel device = Json.fromJson(json, DeviceRegistryApiModel.class);
        return devices.createAsync(device.toServiceModel())
            .thenApply(newDevice -> ok(toJson(new DeviceRegistryApiModel(newDevice))));
    }

    public CompletionStage<Result> delete(final String id) throws IOException, IotHubException {
        return devices.deleteAsync(id)
            .thenApply(result -> ok());
    }
}
