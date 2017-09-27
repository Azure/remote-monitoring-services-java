// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.helpers;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.ExternalDependencyException;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.concurrent.CompletionStage;

@ImplementedBy(HttpClientWrapper.class)
public interface IHttpClientWrapper {

    <T> CompletionStage<T> GetAsync(String uri, String description, Class<T> type, boolean acceptNotFound) throws URISyntaxException, ExternalDependencyException;

    CompletionStage PostAsync(String uri, String description, Object content) throws URISyntaxException, UnsupportedEncodingException, ExternalDependencyException;

    CompletionStage PutAsync(String uri, String description, Object content) throws URISyntaxException, UnsupportedEncodingException, ExternalDependencyException;

    default <T> CompletionStage<T> GetAsync(String uri, String description, Class<T> type) throws URISyntaxException, ExternalDependencyException {
        return GetAsync(uri, description, type, false);
    }

    default CompletionStage PostAsync(String uri, String description) throws UnsupportedEncodingException, ExternalDependencyException, URISyntaxException {
        return PostAsync(uri, description, null);
    }

    default CompletionStage PutAsync(String uri, String description) throws UnsupportedEncodingException, ExternalDependencyException, URISyntaxException {
        return PutAsync(uri, description, null);
    }
}
