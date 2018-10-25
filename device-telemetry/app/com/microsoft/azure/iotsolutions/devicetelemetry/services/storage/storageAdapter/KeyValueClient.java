// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.storage.storageAdapter;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.Status;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServicesConfig;
import org.apache.http.HttpStatus;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

public class KeyValueClient implements IKeyValueClient {

    private final IServicesConfig servicesConfig;
    private String storageAdapterWebserviceUrl;

    private WSClient wsClient;

    @Inject
    public KeyValueClient(final IServicesConfig config, WSClient wsClient) {
        this.servicesConfig = config;
        this.storageAdapterWebserviceUrl = servicesConfig.getKeyValueStorageUrl();
        this.wsClient = wsClient;
    }

    public CompletionStage<Status> pingAsync() {
        WSRequest request = wsClient.url(storageAdapterWebserviceUrl + "/status");
        request.setRequestTimeout(Duration.ofSeconds(10));
        CompletionStage<WSResponse> responsePromise = request.get();
        String name = "KeyValueStorage";

        return responsePromise.handle((result, error) -> {
            if (error != null) {
                return new Status(name, false, error.getMessage());
            } else {
                if (result.getStatus() == HttpStatus.SC_OK) {
                    return new Status(name, true, "Storage adapter alive and well!");
                } else {
                    return new Status(name, false, result.getStatusText());
                }
            }
        });
    }
}
