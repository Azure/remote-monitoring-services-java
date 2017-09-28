// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.http;

import io.netty.handler.codec.http.HttpMethod;
import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import play.Logger;

import java.net.URI;
import java.util.Hashtable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


public class HttpClient implements IHttpClient {

    private static final Hashtable<HttpMethod, Class<?>> requestMap = new Hashtable<HttpMethod, Class<?>>();
    private static final Logger.ALogger log = Logger.of(HttpClient.class);

    static {
        requestMap.put(HttpMethod.GET, HttpGet.class);
        requestMap.put(HttpMethod.DELETE, HttpDelete.class);
        requestMap.put(HttpMethod.HEAD, HttpHead.class);
        requestMap.put(HttpMethod.OPTIONS, HttpOptions.class);
        requestMap.put(HttpMethod.PATCH, HttpPatch.class);
        requestMap.put(HttpMethod.POST, HttpPost.class);
        requestMap.put(HttpMethod.PUT, HttpPut.class);
        requestMap.put(HttpMethod.TRACE, HttpTrace.class);
    }


    private static void setTimeout(
            IHttpRequest request,
            HttpRequestBase httpRequest) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(request.getOptions().getTimeout())
                .build();
        httpRequest.setConfig(requestConfig);
    }

    private static void setContent(IHttpRequest request, HttpMethod httpMethod, HttpEntityEnclosingRequestBase httpRequest) {
        if (httpMethod != HttpMethod.POST && httpMethod != HttpMethod.PUT) return;
        httpRequest.setEntity(request.getContent());
        if (request.getContentType() != null && request.getContent() != null) {
            httpRequest.setHeader("ContentType", request.getContentType());
        }
    }

    private static void setHeaders(IHttpRequest request, HttpRequestBase httpRequest) {
        for (Header header : request.getHeaders()) {
            httpRequest.setHeader(header.getName(), header.getValue());
        }
    }

    private CompletionStage<IHttpResponse> execute(IHttpRequest request, HttpMethod httpMethod) {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpRequestBase httpRequest = (HttpRequestBase) requestMap.get(httpMethod).
                    getConstructor(URI.class).newInstance(request.getUri());
            setTimeout(request, httpRequest);
            if (HttpEntityEnclosingRequestBase.class.isInstance(httpRequest)) {
                HttpEntityEnclosingRequestBase enclosingRequestBase = (HttpEntityEnclosingRequestBase) httpRequest;
                setContent(request, httpMethod, enclosingRequestBase);
                httpRequest = enclosingRequestBase;
            }
            log.debug(String.format("Sending request %s %s ", httpMethod.name(), request.getUri().toString()));
            setHeaders(request, httpRequest);
            CloseableHttpResponse httpResponse = httpclient.execute(httpRequest);
            HttpResponse response = new HttpResponse();
            response.setStatusCode(httpResponse.getStatusLine().getStatusCode());
            response.setHeaders(httpResponse.getAllHeaders());
            String content = EntityUtils.toString(httpResponse.getEntity());
            response.setContent(content);
            log.debug(String.format("Sending request %s %s :content:%s", httpMethod.name(), request.getUri().toString(), content));
            return CompletableFuture.supplyAsync(() -> response);
        } catch (Exception e) {
            log.error("Request failed", e);
            HttpResponse response = new HttpResponse();
            response.setStatusCode(0);
            response.setContent(e.getMessage());
            return CompletableFuture.supplyAsync(() -> response);
        }
    }

    @Override
    public CompletionStage<IHttpResponse> getAsync(IHttpRequest request) {
        return this.execute(request, HttpMethod.GET);
    }

    @Override
    public CompletionStage<IHttpResponse> postAsync(IHttpRequest request) {
        return this.execute(request, HttpMethod.POST);
    }

    @Override
    public CompletionStage<IHttpResponse> putAsync(IHttpRequest request) {
        return this.execute(request, HttpMethod.PUT);
    }

    @Override
    public CompletionStage<IHttpResponse> patchAsync(IHttpRequest request) {
        return this.execute(request, HttpMethod.PATCH);
    }

    @Override
    public CompletionStage<IHttpResponse> deleteAsync(IHttpRequest request) {
        return this.execute(request, HttpMethod.DELETE);
    }

    @Override
    public CompletionStage<IHttpResponse> headAsync(IHttpRequest request) {
        return this.execute(request, HttpMethod.HEAD);
    }

    @Override
    public CompletionStage<IHttpResponse> optionsAsync(IHttpRequest request) {
        return this.execute(request, HttpMethod.OPTIONS);
    }
}
