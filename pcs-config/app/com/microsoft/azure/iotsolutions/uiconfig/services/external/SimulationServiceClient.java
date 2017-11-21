// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.uiconfig.services.http.HttpRequest;
import com.microsoft.azure.iotsolutions.uiconfig.services.http.IHttpClient;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.IServicesConfig;
import play.Logger;
import play.libs.Json;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.concurrent.CompletionStage;

public class SimulationServiceClient implements ISimulationServiceClient {

    private final IHttpClient httpClient;
    private static final Logger.ALogger log = Logger.of(SimulationServiceClient.class);
    private final String serviceUri;

    @Inject
    public SimulationServiceClient(IHttpClient httpClient, IServicesConfig config) {
        this.httpClient = httpClient;
        this.serviceUri = config.getDeviceSimulationApiUrl();
    }

    @Override
    public CompletionStage<HashSet<String>> getDevicePropertyNamesAsync() throws URISyntaxException {
        HttpRequest request = new HttpRequest();
        request.setUriFromString(String.format("%s/DeviceModels", this.serviceUri));
        if (this.serviceUri.toLowerCase().startsWith("https:")) {
            request.getOptions().setAllowInsecureSSLServer(true);
        }
        return this.httpClient.getAsync(request).
                thenApplyAsync(m -> Json.fromJson(Json.parse(m.getContent()), DeviceModelListApiModel.class).GetPropNames());
    }
}
