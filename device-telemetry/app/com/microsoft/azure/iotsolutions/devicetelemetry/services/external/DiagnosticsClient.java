// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.Rules;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.DiagnosticsRequestModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.DiagnosticsConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServicesConfig;
import play.Logger;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import static play.libs.Json.toJson;

public class DiagnosticsClient implements IDiagnosticsClient {

    private final WSClient wsClient;
    private final String diagnosticsEndpointUrl;
    private final int diagnosticsMaxLogRetries;
    private static final Logger.ALogger log = Logger.of(Rules.class);

    private static final int OK = 200;

    @Inject
    public DiagnosticsClient(final WSClient wsClient, final IServicesConfig servicesConfig) {
        this.wsClient = wsClient;
        this.diagnosticsEndpointUrl = servicesConfig.getDiagnosticsConfig().getApiUrl();
        this.diagnosticsMaxLogRetries = servicesConfig.getDiagnosticsConfig().getMaxLogRetries();
    }

    @Override
    public CompletionStage<Void> logEventAsync(String eventName) {
        Hashtable<String, Object> emptyTable = new Hashtable<>();
        return CompletableFuture.runAsync(() -> this.logEvent(eventName, emptyTable));
    }

    @Override
    public CompletionStage<Void> logEventAsync(String eventName, Dictionary<String, Object> eventProperties) {
        return CompletableFuture.runAsync(() -> this.logEvent(eventName, eventProperties));
    }

    private void logEvent(String eventName, Dictionary<String, Object> eventProperties) {
        DiagnosticsRequestModel requestModel = new DiagnosticsRequestModel(eventName, eventProperties);
        int retryCount = 0;
        boolean requestSucceeded = false;
        while (!requestSucceeded && retryCount < this.diagnosticsMaxLogRetries) {
            try {
                this.sendPostRequest(toJson(requestModel)).toCompletableFuture().get();
                requestSucceeded = true;
            } catch (Exception e) {
                retryCount++;
                if (retryCount < this.diagnosticsMaxLogRetries) {
                    log.warn("Failed to log to diagnostics service, retrying");
                } else {
                    log.error("Failed to log to diagnostics service, reached max number of retries");
                }
            }
        }
    }

    private CompletionStage<Object> sendPostRequest(JsonNode jsonData) throws Exception {
        String data = jsonData.toString();
        return this.prepareRequest()
                .post(jsonData.toString())
                .handle((result, error) -> {
                    if (error != null) {
                        log.error("Error logging to diagnostics service {}",
                                error.getMessage());
                        throw new CompletionException(
                                new ExternalDependencyException(error.getMessage()));
                    }

                    if (result.getStatus() != OK) {
                        log.error("Error logging to diagnostics service {}",
                                error.getMessage());
                        throw new CompletionException(
                                new ExternalDependencyException(result.getStatusText()));
                    }
                    return true;
                });
    }



    private WSRequest prepareRequest() {

        String url = this.diagnosticsEndpointUrl + "/diagnosticsevents";

        WSRequest wsRequest = this.wsClient
                .url(url)
                .addHeader("Content-Type", "application/json");

        return wsRequest;
    }

}
