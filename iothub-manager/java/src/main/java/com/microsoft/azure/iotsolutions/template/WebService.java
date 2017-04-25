// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.template;

import akka.Done;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.template.models.Device;
import com.microsoft.azure.iotsolutions.template.models.Building;
import com.microsoft.azure.iotsolutions.template.runtime.IConfig;
import com.microsoft.azure.iotsolutions.template.services.Devices;
import com.microsoft.azure.iotsolutions.template.services.Buildings;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.server.PathMatchers.longSegment;

public class WebService extends AllDirectives {
    private IConfig config;

    @Inject
    public WebService(IConfig config) {
        this.config = config;
    }

    public Route setupRoutes() {
        return route(
            get(() ->
                pathPrefix("devices", () ->
                    path(longSegment(), (Long id) ->
                    {
                        final CompletionStage<Optional<Device>> futureMaybeDevice = Devices.fetchDevice(id);
                        return onSuccess(() -> futureMaybeDevice, maybeDevice ->
                            maybeDevice.map(device -> completeOK(device, Jackson.marshaller()))
                                .orElseGet(() -> complete(StatusCodes.NOT_FOUND, "Device Not Found"))
                        );
                    }))),
            get(() ->
                pathPrefix("devicesv2", () ->
                    path(longSegment(), (Long id) ->
                    {
                        final CompletionStage<Optional<Device>> futureMaybeDevice = Devices.fetchDevice(id);
                        return onSuccess(() -> futureMaybeDevice, maybeDevice ->
                            maybeDevice.map(device -> completeOK(device, Jackson.marshaller()))
                                .orElseGet(() -> complete(StatusCodes.NOT_FOUND, "Device Not Found"))
                        );
                    }))),
            post(() ->
                path("buildings", () ->
                    entity(Jackson.unmarshaller(Building.class), building ->
                    {
                        CompletionStage<Done> futureSaved = Buildings.saveBuilding(building);
                        return onSuccess(() -> futureSaved, done ->
                            complete("Building Created")
                        );
                    })))
        );
    }
}
