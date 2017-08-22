// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.http;

import com.google.inject.ImplementedBy;

import java.util.concurrent.CompletionStage;

@ImplementedBy(HttpClient.class)
public interface IHttpClient {

    CompletionStage<IHttpResponse> getAsync(IHttpRequest request);

    CompletionStage<IHttpResponse> postAsync(IHttpRequest request);

    CompletionStage<IHttpResponse> putAsync(IHttpRequest request);

    CompletionStage<IHttpResponse> patchAsync(IHttpRequest request);

    CompletionStage<IHttpResponse> deleteAsync(IHttpRequest request);

    CompletionStage<IHttpResponse> headAsync(IHttpRequest request);

    CompletionStage<IHttpResponse> optionsAsync(IHttpRequest request);
}
