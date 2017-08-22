// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external;

import com.google.gson.annotations.SerializedName;

import java.util.Hashtable;

public class ValueApiModel {

    @SerializedName("Key")
    private String key;

    @SerializedName("Data")
    private String data;

    @SerializedName("ETag")
    private String eTag;

    @SerializedName("$metadata")
    private Hashtable<String, String> metadata;

    public ValueApiModel() {
    }

    public ValueApiModel(String key, String data, String eTag, Hashtable<String, String> metadata) {
        this.key = key;
        this.data = data;
        this.eTag = eTag;
        this.metadata = metadata;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String geteTag() {
        return eTag;
    }

    public void seteTag(String eTag) {
        this.eTag = eTag;
    }

    public Hashtable<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Hashtable<String, String> metadata) {
        this.metadata = metadata;
    }
}
