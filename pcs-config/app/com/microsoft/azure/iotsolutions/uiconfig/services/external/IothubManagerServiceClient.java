// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.uiconfig.services.http.HttpRequest;
import com.microsoft.azure.iotsolutions.uiconfig.services.http.IHttpClient;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.DeviceTwinName;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.IServicesConfig;
import play.Logger;
import play.libs.Json;

import java.net.URISyntaxException;
import java.util.concurrent.CompletionStage;

public class IothubManagerServiceClient implements IIothubManagerServiceClient {

    private final IHttpClient httpClient;
    private static final Logger.ALogger log = Logger.of(IothubManagerServiceClient.class);
    private final String serviceUri;

    @Inject
    public IothubManagerServiceClient(IHttpClient httpClient, IServicesConfig config) {
        this.httpClient = httpClient;
        this.serviceUri = config.getHubManagerApiUrl();
    }

    @Override
    public CompletionStage<DeviceTwinName> getDeviceTwinNamesAsync() throws URISyntaxException {
        HttpRequest request = new HttpRequest();
        request.setUriFromString(String.format("%s/devices", this.serviceUri));
        if (this.serviceUri.toLowerCase().startsWith("https:")) {
            request.getOptions().setAllowInsecureSSLServer(true);
        }
        return this.httpClient.getAsync(request).
                thenApplyAsync(m ->
                        Json.fromJson(Json.parse(m.getContent()), DeviceListApiModel.class).GetDeviceTwinNames()
                );
    }
}
