// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.controllers;

import akka.actor.ActorSystem;
import akka.actor.Scheduler;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.microsoft.azure.iotsolutions.iothubmanager.services.IDevices;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models.DeviceApiModel;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models.DeviceListApiModel;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.ExecutionContext;
import scala.concurrent.ExecutionContextExecutor;

import java.util.concurrent.*;

import static play.libs.Json.toJson;

// TODO: documentation
// TODO: remove sample code and complete methods

@Singleton
public final class DevicesController extends Controller {

    private final ActorSystem actorSystem;
    private final ExecutionContextExecutor exec;
    private final IDevices devices;

    /**
     * @param actorSystem We need the {@link ActorSystem}'s
     *                    {@link Scheduler} to run code after a delay.
     * @param exec        We need a Java {@link Executor} to apply the result
     *                    of the {@link CompletableFuture} and a Scala
     *                    {@link ExecutionContext} so we can use the Akka {@link Scheduler}.
     *                    An {@link ExecutionContextExecutor} implements both interfaces.
     */
    @Inject
    public DevicesController(
        final ActorSystem actorSystem,
        final ExecutionContextExecutor exec,
        final IDevices devices) {

        this.actorSystem = actorSystem;
        this.exec = exec;
        this.devices = devices;
    }

    public CompletionStage<Result> list() {
        return devices.getListAsync()
            .thenApply(list -> ok(toJson(new DeviceListApiModel(list))));
    }

    public CompletionStage<Result> get(final String id) {
        return devices.getAsync(id)
            .thenApply(device -> ok(toJson(new DeviceApiModel(device))));
    }

    public CompletionStage<Result> post() {
        JsonNode json = request().body().asJson();
        final DeviceApiModel device = Json.fromJson(json, DeviceApiModel.class);
        return devices.createAsync(device.toServiceModel())
            .thenApply(newDevice -> ok(toJson(new DeviceApiModel(newDevice))));
    }
}
