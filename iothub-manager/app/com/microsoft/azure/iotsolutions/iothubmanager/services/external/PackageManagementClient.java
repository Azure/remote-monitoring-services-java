// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.external;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.ResourceNotFoundException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.helpers.HttpRequestHelper;
import com.microsoft.azure.iotsolutions.iothubmanager.services.runtime.IServicesConfig;
import play.Logger;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;

import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;

public class PackageManagementClient implements IPackageManagementClient {
    private WSClient wsClient;
    private static final Logger.ALogger log = Logger.of(PackageManagementClient.class);
    private final String serviceUri;

    @Inject
    public PackageManagementClient(WSClient wsClient, IServicesConfig config) {
        this.wsClient = wsClient;
        this.serviceUri = config.getConfigServiceUrl() + "/packages";
    }

    @Override
    public CompletionStage<PackageApiModel> getPackageAsync(String packageId) throws ResourceNotFoundException,
            ExternalDependencyException {
        final WSRequest request = wsClient.url(String.format("%s/%s", this.serviceUri, packageId));

        return request.get()
                .thenApplyAsync(m -> {
                    try {
                        HttpRequestHelper.checkStatusCode(m, request);
                    } catch (Exception e) {
                        throw new CompletionException("Unable to get package " + packageId, e);
                    }
                    return Json.fromJson(m.asJson(), PackageApiModel.class);
                });
    }
}
