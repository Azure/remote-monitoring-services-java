// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.template.services;

import akka.Done;
import com.microsoft.azure.iotsolutions.template.models.Building;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class Buildings {

    // (fake) async database query api
    public static CompletionStage<Done> saveBuilding(final Building building) {
        return CompletableFuture.completedFuture(Done.getInstance());
    }
}
