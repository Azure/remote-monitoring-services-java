// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.controllers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.microsoft.azure.iotsolutions.uiconfig.services.ICache;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.BaseException;
import com.microsoft.azure.iotsolutions.uiconfig.webservice.filters.DepressedFilter;
import com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.models.DeviceGroupFiltersApiModel;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static play.libs.Json.toJson;

@Singleton
public class DeviceGroupFiltersController extends Controller {
    private final ICache cache;

    @Inject
    public DeviceGroupFiltersController(ICache cache) {
        this.cache = cache;
    }

    public CompletionStage<Result> getAllAsync() throws BaseException {
        return cache.getCacheAsync().thenApplyAsync(m ->
                ok(toJson(new DeviceGroupFiltersApiModel(m)))
        );
    }

    public CompletionStage<Result> setAsync() throws BaseException, ExecutionException, InterruptedException {
        DeviceGroupFiltersApiModel input = Json.fromJson(request().body().asJson(), DeviceGroupFiltersApiModel.class);
        return cache.setCacheAsync(input.ToServiceModel()).thenApplyAsync(m ->
                ok()
        );
    }

    @With(DepressedFilter.class)
    public CompletionStage<Result> rebuildAsync() throws Exception {
        return cache.rebuildCacheAsync(true).thenApplyAsync(m -> ok());
    }
}
