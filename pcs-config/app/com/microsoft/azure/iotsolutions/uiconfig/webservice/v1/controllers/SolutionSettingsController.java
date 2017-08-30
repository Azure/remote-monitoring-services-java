// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.controllers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.microsoft.azure.iotsolutions.uiconfig.services.IStorage;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.*;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.LogoServiceModel;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Base64;
import java.util.concurrent.CompletionStage;

import static play.libs.Json.toJson;

@Singleton
public final class SolutionSettingsController extends Controller {

    private final IStorage storage;

    @Inject
    public SolutionSettingsController(IStorage storage) {
        this.storage = storage;
    }

    public CompletionStage<Result> getThemeAsync() throws BaseException {
        return storage.getThemeAsync()
            .thenApply(theme -> ok(toJson(theme)));
    }

    public CompletionStage<Result> setThemeAsync() throws BaseException {
        Object theme = Json.fromJson(request().body().asJson(), Object.class);
        return storage.setThemeAsync(theme)
            .thenApply(result -> ok(toJson(result)));
    }

    public CompletionStage<Result> getLogoAsync() throws BaseException {
        return storage.getLogoAsync()
            .thenApply(result -> ok(setImageResponse(result)));
    }

    public CompletionStage<Result> setLogoAsync() throws BaseException {
        byte[] bytes = request().body().asBytes().toByteBuffer().array();
        LogoServiceModel model = new LogoServiceModel();
        model.setType(request().contentType().orElse("application/octet-stream"));
        model.setImage(Base64.getEncoder().encodeToString(bytes));
        return storage.setLogoAsync(model)
            .thenApply(result -> {
                response().setHeader(CONTENT_TYPE, model.getType());
                return ok(setImageResponse(result));
            });
    }

    private byte[] setImageResponse(LogoServiceModel model) {
        return Base64.getDecoder().decode(model.getImage().getBytes());
    }
}
