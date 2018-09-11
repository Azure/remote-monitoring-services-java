// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.http;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import play.libs.Json;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class HttpRequest implements IHttpRequest {

    private URI uri;
    private List<Header> headers = new ArrayList<Header>();
    private String contentType;
    private HttpRequestOptions options = new HttpRequestOptions();
    private HttpEntity content;

    private final String defaultMediaType = "application/json";
    private final Charset defaultEncoding = Charset.forName("UTF-8");

    private static <T> String toJson(T o) {
        return Json.stringify(Json.toJson(o));
    }

    public HttpRequest() {
    }

    public HttpRequest(URI uri) {
        this.uri = uri;
    }

    public HttpRequest(String uri) throws URISyntaxException {
        this.uri = new URI(uri);
    }

    @Override
    public URI getUri() {
        return uri;
    }

    @Override
    public void setUri(URI uri) {
        this.uri = uri;
    }

    @Override
    public Header[] getHeaders() {
        Header[] header = new Header[headers.size()];
        headers.toArray(header);
        return header;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public HttpEntity getContent() {
        return content;
    }

    @Override
    public HttpRequestOptions getOptions() {
        return options;
    }

    @Override
    public void addHeader(String name, String value) {
        this.headers.add(new BasicHeader(name, name));
    }

    @Override
    public void setUriFromString(String uri) throws URISyntaxException {
        this.uri = new URI(uri);
    }

    @Override
    public void setContent(String content) throws UnsupportedEncodingException {
        this.setContent(content, this.defaultEncoding, this.defaultMediaType);
    }

    @Override
    public void setContent(String content, Charset encoding) throws UnsupportedEncodingException {
        this.setContent(content, encoding, this.defaultMediaType);
    }

    @Override
    public void setContent(String content, Charset encoding, String mediaType) throws UnsupportedEncodingException {
        this.content = new StringEntity(content, mediaType, encoding.name());
        this.contentType = mediaType;
    }

    @Override
    public void setContent(StringEntity StringContent) throws UnsupportedEncodingException {
        this.content = StringContent;
        this.contentType = this.defaultMediaType;
    }

    @Override
    public <T> void setContent(T sourceObject) throws UnsupportedEncodingException {
        this.setContent(sourceObject, this.defaultEncoding, this.defaultMediaType);
    }

    @Override
    public <T> void setContent(T sourceObject, Charset encoding) throws UnsupportedEncodingException {
        this.setContent(sourceObject, encoding, this.defaultMediaType);
    }

    @Override
    public <T> void setContent(T sourceObject, Charset encoding, String mediaType) throws UnsupportedEncodingException {
        String content = toJson(sourceObject);
        this.content = new StringEntity(content, mediaType, encoding.name());
        this.contentType = mediaType;
    }
}
