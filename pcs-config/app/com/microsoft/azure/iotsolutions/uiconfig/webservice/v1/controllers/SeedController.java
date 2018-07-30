// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.controllers;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.uiconfig.services.ISeed;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.ExternalDependencyException;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.concurrent.CompletionStage;

public class SeedController extends Controller {

    private final ISeed seed;

    @Inject
    public SeedController(ISeed seed) {
        this.seed = seed;
    }

    public CompletionStage<Result> postAsync() throws ExternalDependencyException {
        return seed.trySeedAsync().thenApplyAsync(m -> ok());
    }
}
