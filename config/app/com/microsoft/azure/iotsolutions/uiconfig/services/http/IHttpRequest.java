// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.http;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

public interface IHttpRequest {

    URI getUri();

    void setUri(URI uri);

    Header[] getHeaders();

    String getContentType();

    HttpEntity getContent();

    HttpRequestOptions getOptions();

    void addHeader(String name, String value);

    void setUriFromString(String uri) throws URISyntaxException;

    void setContent(String content) throws UnsupportedEncodingException;

    void setContent(String content, Charset encoding) throws UnsupportedEncodingException;

    void setContent(String content, Charset encoding, String mediaType) throws UnsupportedEncodingException;

    void setContent(StringEntity StringContent) throws UnsupportedEncodingException;

    <T> void setContent(T sourceObject) throws UnsupportedEncodingException;

    <T> void setContent(T sourceObject, Charset encoding) throws UnsupportedEncodingException;

    <T> void setContent(T sourceObject, Charset encoding, String mediaType) throws UnsupportedEncodingException;

}
