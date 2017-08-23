// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.controllers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.microsoft.azure.iotsolutions.uiconfig.services.IStorage;
import com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.models.DeviceGroupApiModel;
import com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.models.DeviceGroupListApiModel;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.concurrent.CompletionStage;

import static play.libs.Json.toJson;

@Singleton
public final class DeviceGroupController extends Controller {

    private final IStorage storage;

    @Inject
    public DeviceGroupController(IStorage storage) {
        this.storage = storage;
    }

    public CompletionStage<Result> getAllAsync() throws UnsupportedEncodingException, URISyntaxException {
        return storage.getAllDeviceGroupsAsync().thenApplyAsync(m -> ok(toJson(new DeviceGroupListApiModel(m))));
    }

    public CompletionStage<Result> getAsync(String id) throws UnsupportedEncodingException, URISyntaxException {
        return storage.getDeviceGroupAsync(id).thenApplyAsync(m -> ok(toJson(new DeviceGroupApiModel(m))));
    }

    public CompletionStage<Result> createAsync() throws UnsupportedEncodingException, URISyntaxException {
        DeviceGroupApiModel input = Json.fromJson(request().body().asJson(), DeviceGroupApiModel.class);
        return storage.createDeviceGroupAsync(input.ToServiceModel()).thenApplyAsync(m -> ok(toJson(new DeviceGroupApiModel(m))));
    }

    public CompletionStage<Result> updateAsync(String id) throws UnsupportedEncodingException, URISyntaxException {
        DeviceGroupApiModel input = Json.fromJson(request().body().asJson(), DeviceGroupApiModel.class);
        return storage.updateDeviceGroupAsync(id, input.ToServiceModel(), input.getETag()).thenApplyAsync(m -> ok(toJson(new DeviceGroupApiModel(m))));
    }

    public CompletionStage<Result> deleteAsync(String id) throws UnsupportedEncodingException, URISyntaxException {
        return storage.deleteDeviceGroupAsync(id).thenApplyAsync(m -> ok());
    }
}
