// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.template.services;

import com.microsoft.azure.iotsolutions.template.models.Device;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class Devices {

    // (fake) async database query api
    public static CompletionStage<Optional<Device>> fetchDevice(long deviceId) {
        return CompletableFuture.completedFuture(Optional.of(new Device("foo", deviceId)));
    }
}
