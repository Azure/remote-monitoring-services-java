// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.controllers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.microsoft.azure.iotsolutions.iothubmanager.services.IDeviceProperties;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.BaseException;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models.DevicePropertiesApiModel;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.concurrent.CompletionStage;

import static play.libs.Json.toJson;

@Singleton
public class DevicePropertiesController extends Controller {
    private final IDeviceProperties deviceProperties;

    @Inject
    public DevicePropertiesController(IDeviceProperties deviceProperties) {
        this.deviceProperties = deviceProperties;
    }

    public CompletionStage<Result> getAllAsync() throws BaseException {
        return this.deviceProperties.getListAsync().thenApplyAsync(m
            -> ok(toJson(new DevicePropertiesApiModel(m))));
    }
}
