// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.*;
import com.microsoft.azure.iotsolutions.uiconfig.services.http.HttpRequest;
import com.microsoft.azure.iotsolutions.uiconfig.services.http.IHttpClient;
import com.microsoft.azure.iotsolutions.uiconfig.services.http.IHttpRequest;
import com.microsoft.azure.iotsolutions.uiconfig.services.http.IHttpResponse;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.IServicesConfig;
import org.apache.http.HttpStatus;
import play.Logger;
import play.libs.Json;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;

public class StorageAdapterClient implements IStorageAdapterClient {
    private final IHttpClient httpClient;
    private static final Logger.ALogger log = Logger.of(StorageAdapterClient.class);
    private final String serviceUri;

    private static <T> String toJson(T o) {
        return Json.stringify(Json.toJson(o));
    }

    private static <A> A fromJson(String json, Class<A> clazz) {
        return Json.fromJson(Json.parse(json), clazz);
    }

    @Inject
    public StorageAdapterClient(IHttpClient httpClient, IServicesConfig config) {
        this.httpClient = httpClient;
        this.serviceUri = config.getStorageAdapterApiUrl();
    }

    @Override
    public CompletionStage<ValueApiModel> getAsync(String collectionId, String key) throws ResourceNotFoundException {
        try {
            HttpRequest request = CreateRequest(String.format("collections/%s/values/%s", collectionId, key));
            IHttpResponse response = httpClient.getAsync(request).toCompletableFuture().get();
            CheckStatusCode(response, request);
            return CompletableFuture.supplyAsync(() -> fromJson(response.getContent(), ValueApiModel.class));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CompletionException("Unable to retrieve " + String.format("collections/%s/values/%s", collectionId, key), e);
        }
    }

    @Override
    public CompletionStage<ValueListApiModel> getAllAsync(String collectionId) throws BaseException {
        HttpRequest request = CreateRequest(String.format("collections/%s/values", collectionId));
        return httpClient.getAsync(request).thenApplyAsync(m -> {
            try {
                CheckStatusCode(m, request);
                return fromJson(m.getContent(), ValueListApiModel.class);
            } catch (Exception e) {
                throw new CompletionException("Unable to retrieve " + request.getUri(), e);
            }
        });
    }

    @Override
    public CompletionStage<ValueApiModel> createAsync(String collectionId, String value) throws BaseException {
        ValueApiModel model = new ValueApiModel();
        model.setData(value);
        HttpRequest request = CreateRequest(String.format("collections/%s/values", collectionId), model);
        return httpClient.postAsync(request).thenApplyAsync(m -> {
            try {
                CheckStatusCode(m, request);
                return fromJson(m.getContent(), ValueApiModel.class);
            } catch (Exception e) {
                throw new CompletionException("Unable to create resource " + request.getUri(), e);
            }
        });
    }

    @Override
    public CompletionStage<ValueApiModel> updateAsync(String collectionId, String key, String value, String etag) throws BaseException {
        ValueApiModel model = new ValueApiModel();
        model.setData(value);
        model.setETag(etag);
        HttpRequest request = CreateRequest(String.format("collections/%s/values/%s", collectionId, key), model);
        return httpClient.putAsync(request).thenApplyAsync(m -> {
            try {
                CheckStatusCode(m, request);
                return fromJson(m.getContent(), ValueApiModel.class);
            } catch (Exception e) {
                throw new CompletionException("Unable to update resource " + request.getUri(), e);
            }
        });
    }

    @Override
    public CompletionStage deleteAsync(String collectionId, String key) throws BaseException {
        HttpRequest request = CreateRequest(String.format("collections/%s/values/%s", collectionId, key));
        return httpClient.deleteAsync(request).thenAcceptAsync(m -> {
            try {
                CheckStatusCode(m, request);
            } catch (Exception e) {
                throw new CompletionException("Unable to delete resource " + request.getUri(), e);
            }
        });
    }

    private HttpRequest CreateRequest(String path, ValueApiModel content) throws InvalidConfigurationException {
        try {
            HttpRequest request = new HttpRequest();
            request.setUriFromString(serviceUri + "/" + path);
            request.getOptions().setAllowInsecureSSLServer(true);
            if (content != null) {
                request.setContent(content);
            }
            return request;
        } catch (UnsupportedEncodingException | URISyntaxException e) {
            throw new InvalidConfigurationException("Unable to create http request", e);
        }
    }

    private HttpRequest CreateRequest(String path) throws InvalidConfigurationException {
        return CreateRequest(path, null);
    }

    private void CheckStatusCode(IHttpResponse response, IHttpRequest request) throws BaseException {
        if (response.isSuccessStatusCode()) {
            return;
        }
        log.info(String.format("StorageAdapter returns %s for request %s",
                response.getStatusCode(), request.getUri().toString()));
        switch (response.getStatusCode()) {
            case HttpStatus.SC_NOT_FOUND:
                throw new ResourceNotFoundException(
                        response.getContent() + ", request URL = " + request.getUri().toString());

            case HttpStatus.SC_CONFLICT:
                throw new ConflictingResourceException(
                        response.getContent() + ", request URL = " + request.getUri().toString());

            default:
                throw new ExternalDependencyException(
                        String.format("Http request failed, status code = %s, content = %s, request URL = %s", response.getStatusCode(), response.getContent(), request.getUri().toString()));
        }
    }
}
