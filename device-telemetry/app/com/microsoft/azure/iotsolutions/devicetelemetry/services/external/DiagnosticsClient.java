// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.Rules;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.DiagnosticsRequestModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServicesConfig;
import org.apache.http.HttpStatus;
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
    private final boolean canWriteToDiagnostics;

    private static final Logger.ALogger log = Logger.of(Rules.class);

    @Inject
    public DiagnosticsClient(final WSClient wsClient, final IServicesConfig servicesConfig) {
        this.wsClient = wsClient;
        this.diagnosticsEndpointUrl = servicesConfig.getDiagnosticsConfig().getApiUrl();
        if(this.diagnosticsEndpointUrl == null || this.diagnosticsEndpointUrl.length() == 0) {
            this.log.error("No diagnostics url given, cannot write to diagnostics");
            this.canWriteToDiagnostics = false;
        } else {
            this.canWriteToDiagnostics = true;
        }
        this.diagnosticsMaxLogRetries = servicesConfig.getDiagnosticsConfig().getMaxLogRetries();
    }

    @Override
    public CompletionStage<Void> logEventAsync(String eventName) {
        Dictionary<String, Object> emptyTable = new Hashtable<>();
        return CompletableFuture.runAsync(() -> this.logEvent(eventName, emptyTable));
    }

    @Override
    public CompletionStage<Void> logEventAsync(String eventName, Dictionary<String, Object> eventProperties) {
        return CompletableFuture.runAsync(() -> this.logEvent(eventName, eventProperties));
    }

    @Override
    public boolean canWriteToDiagnostics() {
        return this.canWriteToDiagnostics;
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
                    log.warn("Failed to log to diagnostics service, " + (this.diagnosticsMaxLogRetries - retryCount) + " retries remaining");
                } else {
                    log.error("Failed to log to diagnostics service, reached max number of retries");
                }
            }
        }
    }

    private CompletionStage<Object> sendPostRequest(JsonNode jsonData) {
        return this.prepareRequest()
                .post(jsonData.toString())
                .handle((result, error) -> {
                    if (error != null) {
                        log.error("Error logging to diagnostics service {}",
                            error.getMessage());
                        throw new CompletionException(
                            new ExternalDependencyException(error.getMessage()));
                    }

                    if (result.getStatus() != HttpStatus.SC_OK) {
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
