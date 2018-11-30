// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.controllers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.microsoft.azure.iotsolutions.uiconfig.services.IStorage;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.BaseException;
import com.microsoft.azure.iotsolutions.uiconfig.webservice.auth.Authorize;
import com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.models.ConfigTypeListApiModel;
import com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.models.PackageApiModel;
import play.mvc.Result;
import java.util.concurrent.CompletionStage;
import static play.libs.Json.toJson;
import static play.mvc.Results.ok;

@Singleton
public class ConfigTypesController {

    private final IStorage storage;

    @Inject
    public ConfigTypesController(IStorage storage) {
        this.storage = storage;
    }

    /**
     * Get a previously created config-types from storage.
     * @return {@link ConfigTypeListApiModel}
     */
    @Authorize("ReadAll")
    public CompletionStage<Result> getAllConfigTypesAsync() throws BaseException {
        return storage.getAllConfigTypesAsync().thenApplyAsync(m -> ok(toJson(new ConfigTypeListApiModel(m))));
    }
}
