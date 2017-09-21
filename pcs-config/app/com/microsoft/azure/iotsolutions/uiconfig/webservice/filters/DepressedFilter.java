// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.webservice.filters;

import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class DepressedFilter extends play.mvc.Action.Simple {
    @Override
    public CompletionStage<Result> call(Http.Context ctx) {
        Object content = Json.fromJson(Json.parse("{\"msg\":\"Not used for now , maybe used some time later.\"}"), Object.class);
        return CompletableFuture.supplyAsync(() -> forbidden(Json.toJson(content)));
    }
}
