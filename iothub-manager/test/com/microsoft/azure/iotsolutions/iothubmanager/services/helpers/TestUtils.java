// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.helpers;

import akka.util.ByteString;
import play.http.HttpEntity;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;

public class TestUtils {
    public static String getString(Result result) {
        ByteString bs = ((HttpEntity.Strict) result.body()).data();
        return bs.utf8String();
    }

    public static <T> T getResult(Result result, Class<T> clazz) {
        final String jsonResult = getString(result);
        return Json.fromJson(Json.parse(jsonResult), clazz);
    }

    public static Http.Response setRequest(Object obj) {
        final String toJson = Json.toJson(obj).toString();
        return setRequest(toJson);
    }

    public static Http.Response setRequest(String body) {
        Http.Request mockRequest = mock(Http.Request.class);
        when(mockRequest.body()).thenReturn(new Http.RequestBody(Json.parse(body)));
        Http.Response mockResponse = mock(Http.Response.class);
        doNothing().when(mockResponse).setHeader(
                any(String.class), any(String.class));
        Http.Context mockContext = mock(Http.Context.class);
        when(mockContext.request()).thenReturn(mockRequest);
        when(mockContext.response()).thenReturn(mockResponse);
        Http.Context.current.set(mockContext);
        return mockResponse;
    }
}