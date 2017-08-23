// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.controllers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.microsoft.azure.iotsolutions.uiconfig.services.IStorage;
import play.mvc.Result;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.concurrent.CompletionStage;

import static play.libs.Json.fromJson;
import static play.libs.Json.toJson;
import static play.mvc.Controller.request;
import static play.mvc.Results.ok;

@Singleton
public class UserSettingsController {

    private IStorage storage;

    @Inject
    public UserSettingsController(IStorage storage) {
        this.storage = storage;
    }

    public CompletionStage<Result> getUserSettingAsync(String id) {
        return storage.getUserSetting(id)
                .thenApply(result -> ok(toJson(result)));
    }

    public CompletionStage<Result> setUserSettingAsync(String id) throws UnsupportedEncodingException, URISyntaxException {
        Object setting = fromJson(request().body().asJson(), Object.class);
        return storage.setUserSetting(id, setting)
                .thenApply(result -> ok(toJson(result)));
    }
}
