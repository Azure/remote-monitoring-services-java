// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.controllers;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.iothubmanager.services.IDevices;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.auth.Authorize;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models.TwinPropertiesApiModel;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models.TwinPropertiesListApiModel;
import org.apache.commons.lang3.StringUtils;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import java.util.concurrent.CompletionStage;

import static play.libs.Json.toJson;

public class ModulesController extends Controller {
    private final IDevices deviceService;
    private final String ContinuationTokenName = "x-ms-continuation";

    @Inject
    public ModulesController(final IDevices deviceService) {
        this.deviceService = deviceService;
    }

    /**
     * Retrieve
     * @param deviceId
     * @param moduleId
     * @return
     * @throws ExternalDependencyException
     * @throws InvalidInputException
     */
    @Authorize("ReadAll")
    public CompletionStage<Result> getModuleTwinAsync(String deviceId, String moduleId) throws
            ExternalDependencyException, InvalidInputException {
        if (StringUtils.isEmpty(deviceId)) {
            throw new InvalidInputException("Missing needed deviceId parameter");
        }

        if (StringUtils.isEmpty(moduleId)) {
            throw new InvalidInputException("Missing needed moduleId parameter");
        }

        return deviceService.getModuleTwinAsync(deviceId, moduleId)
                .thenApply(devices -> ok(toJson(new TwinPropertiesApiModel(deviceId, moduleId, devices.getProperties()))));
    }

    @Authorize("ReadAll")
    public CompletionStage<Result> getModuleTwinsAsync(String query) throws ExternalDependencyException {
        String continuationToken = StringUtils.EMPTY;

        if (request().getHeaders().contains(ContinuationTokenName)) {
            continuationToken = request().getHeaders().getAll(ContinuationTokenName).get(0);
        }
        return deviceService.getModuleTwinsByQueryAsync(query, continuationToken)
                .thenApply(twins -> ok(toJson(new TwinPropertiesListApiModel(twins))));
    }

    @Authorize("ReadAll")
    public CompletionStage<Result> queryModuleTwinsAsync() throws ExternalDependencyException {
        String query;
        if (request().getHeaders().get(CONTENT_TYPE).isPresent() &&
            request().getHeaders().get(CONTENT_TYPE).get().equals(Http.MimeTypes.JSON)) {
            query = request().body().asJson().asText();
        } else {
            query = request().body().asText();
        }

        return this.getModuleTwinsAsync(query);
    }
}
