// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.helpers;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.uiconfig.services.http.HttpRequest;
import com.microsoft.azure.iotsolutions.uiconfig.services.http.IHttpClient;
import com.microsoft.azure.iotsolutions.uiconfig.services.http.IHttpResponse;
import play.Logger;
import play.libs.Json;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class HttpClientWrapper implements IHttpClientWrapper {

    private static final Logger.ALogger log = Logger.of(HttpClientWrapper.class);
    private IHttpClient client;

    @Inject
    public HttpClientWrapper(
            IHttpClient client) {
        this.client = client;
    }

    @Override
    public <T> CompletionStage<T> getAsync(String uri, String description, Class<T> type, boolean acceptNotFound) throws ExternalDependencyException, URISyntaxException {
        HttpRequest request = new HttpRequest();
        request.setUriFromString(uri);
        request.addHeader("Accept", "application/json");
        request.addHeader("Cache-Control", "no-cache");
        request.addHeader("User-Agent", "Config");
        if (uri.toLowerCase().startsWith("https:")) {
            request.getOptions().setAllowInsecureSSLServer(true);
        }

        IHttpResponse response;
        try {
            response = this.client.getAsync(request).toCompletableFuture().get();
        } catch (Exception e) {
            this.log.error("Request failed");
            throw new ExternalDependencyException(String.format("Failed to load %s", description));
        }

        if (response.getStatusCode() == 404 && acceptNotFound) {
            return CompletableFuture.supplyAsync(() -> null);
        }

        if (response.getStatusCode() != 200) {
            this.log.error("Request failed");
            throw new ExternalDependencyException(String.format("Unable to load %s", description));
        }

        try {
            return CompletableFuture.supplyAsync(() -> Json.fromJson(Json.parse(response.getContent()), type));
        } catch (Exception e) {
            this.log.error(String.format("Could not parse result from %s: %s", uri, e.getMessage()));
            throw new ExternalDependencyException(String.format("Could not parse result from %s", uri));
        }
    }

    @Override
    public CompletionStage postAsync(String uri, String description, Object content) throws URISyntaxException, UnsupportedEncodingException, ExternalDependencyException {
        HttpRequest request = new HttpRequest();
        request.setUriFromString(uri);
        request.addHeader("Accept", "application/json");
        request.addHeader("Cache-Control", "no-cache");
        request.addHeader("User-Agent", "Config");
        if (uri.toLowerCase().startsWith("https:")) {
            request.getOptions().setAllowInsecureSSLServer(true);
        }

        if (content != null) {
            request.setContent(content);
        }

        IHttpResponse response;
        try {
            response = this.client.getAsync(request).toCompletableFuture().get();
        } catch (Exception e) {
            this.log.error("Request failed");
            throw new ExternalDependencyException(String.format("Failed to post %s", description));
        }
        if (response.getStatusCode() != 200) {
            this.log.error("Request failed");
            throw new ExternalDependencyException(String.format("Unable to post %s", description));
        }
        return CompletableFuture.runAsync(() -> {
        });
    }

    @Override
    public CompletionStage putAsync(String uri, String description, Object content) throws URISyntaxException, UnsupportedEncodingException, ExternalDependencyException {
        HttpRequest request = new HttpRequest();
        request.setUriFromString(uri);
        request.addHeader("Accept", "application/json");
        request.addHeader("Cache-Control", "no-cache");
        request.addHeader("User-Agent", "Config");
        if (uri.toLowerCase().startsWith("https:")) {
            request.getOptions().setAllowInsecureSSLServer(true);
        }

        if (content != null) {
            request.setContent(content);
        }

        IHttpResponse response;
        try {
            response = this.client.putAsync(request).toCompletableFuture().get();
        } catch (Exception e) {
            this.log.error("Request failed");
            throw new ExternalDependencyException(String.format("Failed to put %s", description));
        }

        if (response.getStatusCode() != 200) {
            this.log.error("Request failed");
            throw new ExternalDependencyException(String.format("Unable to put %s", description));
        }
        return CompletableFuture.runAsync(() -> {
        });
    }
}
