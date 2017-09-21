// Copyright (c) Microsoft. All rights reserved.


package com.microsoft.azure.iotsolutions.uiconfig.services.http;

import org.apache.http.Header;
import org.apache.http.HttpStatus;


public class HttpResponse implements IHttpResponse {

    private int statusCode;
    private Header[] headers;
    private String content;
    private final int TooManyRequests = 429;

    public HttpResponse() {
    }

    public HttpResponse(int statusCode, Header[] headers, String content) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.content = content;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public Header[] getHeaders() {
        return headers;
    }

    public void setHeaders(Header[] headers) {
        this.headers = headers;
    }

    @Override
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean getIsRetriableError() {
        return this.statusCode == HttpStatus.SC_NOT_FOUND ||
                this.statusCode == HttpStatus.SC_REQUEST_TIMEOUT ||
                (int) this.statusCode == TooManyRequests;
    }
}
