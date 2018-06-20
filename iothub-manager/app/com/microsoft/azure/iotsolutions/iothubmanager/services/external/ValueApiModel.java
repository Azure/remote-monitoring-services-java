// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.external;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

public class ValueApiModel {

    private String key;
    private String data;
    private String eTag;
    private HashMap<String, String> metadata;

    public ValueApiModel() {
    }

    public ValueApiModel(String key, String data, String eTag, HashMap<String, String> metadata) {
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
    public HashMap<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(HashMap<String, String> metadata) {
        this.metadata = metadata;
    }
}
