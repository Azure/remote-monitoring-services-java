// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.microsoft.azure.iotsolutions.iothubmanager.services.IDevices;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.*;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models.DeviceRegistryApiModel;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models.DeviceListApiModel;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;
import play.libs.Json;
import play.mvc.*;

import java.io.IOException;
import java.util.concurrent.*;

import static play.libs.Json.toJson;
import static play.libs.Json.fromJson;

@Singleton
public final class DevicesController extends Controller {

    private final IDevices devices;

    final String ContinuationTokenName = "x-ms-continuation";

    @Inject
    public DevicesController(final IDevices devices) {
        this.devices = devices;
    }

    public CompletionStage<Result> getDevicesAsync(String query) throws IOException, IotHubException, BaseException {
        String continuationToken = "";
        if (request().getHeaders().contains(ContinuationTokenName)) {
            continuationToken = request().getHeaders().getAll(ContinuationTokenName).get(0);
        }
        return devices.queryAsync(query, continuationToken)
            .thenApply(devices -> ok(toJson(new DeviceListApiModel(devices))));
    }

    public CompletionStage<Result> queryDevicesAsync() throws BaseException, IOException, IotHubException {
        String continuationToken = "";
        String query;
        if (request().getHeaders().get(CONTENT_TYPE).get().equals(Http.MimeTypes.JSON)) {
            query = Json.stringify(request().body().asJson());
        } else {
            query = request().body().asText();
        }

        if (request().getHeaders().contains(ContinuationTokenName)) {
            continuationToken = request().getHeaders().getAll(ContinuationTokenName).get(0);
        }

        return devices.queryAsync(query, continuationToken)
            .thenApply(devices -> ok(toJson(new DeviceListApiModel(devices))));
    }

    public CompletionStage<Result> getDeviceAsync(final String id) throws IOException, IotHubException {
        return devices.getAsync(id)
            .thenApply(device -> ok(toJson(new DeviceRegistryApiModel(device))));
    }

    public CompletionStage<Result> postAsync() throws BaseException, IOException, IotHubException {
        JsonNode json = request().body().asJson();
        final DeviceRegistryApiModel device = fromJson(json, DeviceRegistryApiModel.class);
        return devices.createAsync(device.toServiceModel())
            .thenApply(newDevice -> ok(toJson(new DeviceRegistryApiModel(newDevice))));
    }

    public CompletionStage<Result> putAsync(final String id) throws BaseException, IOException, IotHubException {
        JsonNode json = request().body().asJson();
        final DeviceRegistryApiModel device = fromJson(json, DeviceRegistryApiModel.class);
        return devices.createOrUpdateAsync(id, device.toServiceModel())
            .thenApply(newDevice -> ok(toJson(new DeviceRegistryApiModel(newDevice))));
    }

    public CompletionStage<Result> deleteAsync(final String id) throws IOException, IotHubException {
        return devices.deleteAsync(id)
            .thenApply(result -> ok());
    }
}
