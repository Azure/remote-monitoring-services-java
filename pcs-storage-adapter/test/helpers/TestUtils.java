// Copyright (c) Microsoft. All rights reserved.

package helpers;

import akka.util.ByteString;
import play.http.HttpEntity;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import static org.mockito.Mockito.*;

public class TestUtils {
    public static String getString(Result result) {
        ByteString bs = ((HttpEntity.Strict) result.body()).data();
        return bs.utf8String();
    }

    public static byte[] getBytes(Result result) {
        ByteString bs = ((HttpEntity.Strict) result.body()).data();
        return bs.toByteBuffer().array();
    }

    public static void setRequest(String body) {
        Http.Request mockRequest = mock(Http.Request.class);
        when(mockRequest.body()).thenReturn(new Http.RequestBody(Json.parse(body)));
        Http.Response mockResponse = mock(Http.Response.class);
        doNothing().when(mockResponse).setHeader(
                any(String.class), any(String.class));
        Http.Context mockContext = mock(Http.Context.class);
        when(mockContext.request()).thenReturn(mockRequest);
        when(mockContext.response()).thenReturn(mockResponse);
        Http.Context.current.set(mockContext);
    }
}
