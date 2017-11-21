// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Hashtable;

public class ValueApiModel {

    private String key;
    private String data;
    private String eTag;
    private Hashtable<String, String> metadata;

    public ValueApiModel() {
    }

    public ValueApiModel(String key, String data, String eTag, Hashtable<String, String> metadata) {
        this.key = key;
        this.data = data;
        this.eTag = eTag;
        this.metadata = metadata;
    }

    @JsonProperty("Key")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @JsonProperty("Data")
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @JsonProperty("ETag")
    public String getETag() {
        return eTag;
    }

    public void setETag(String eTag) {
        this.eTag = eTag;
    }

    @JsonProperty("$metadata")
    public Hashtable<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Hashtable<String, String> metadata) {
        this.metadata = metadata;
    }
}
