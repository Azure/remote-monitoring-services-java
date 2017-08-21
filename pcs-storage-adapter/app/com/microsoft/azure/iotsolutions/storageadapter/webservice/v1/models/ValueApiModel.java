// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.microsoft.azure.iotsolutions.storageadapter.services.models.ValueServiceModel;
import com.microsoft.azure.iotsolutions.storageadapter.webservice.v1.Version;

import java.util.Dictionary;
import java.util.Hashtable;

@JsonPropertyOrder({"Status", "CurrentTime", "StartTime", "UpTime", "Properties", "Dependencies", "$metadata"})
public final class ValueApiModel {

    private String key;
    private String data;
    private String etag;
    private Hashtable<String, String> $metadata;

    public ValueApiModel(final ValueServiceModel model) {
        this.key = model.Key;
        this.data = model.Data;
        this.etag = model.ETag;
        this.$metadata = new Hashtable<String, String>() {{
            put("$type", "Status;" + Version.Number);
            put("$uri", "/" + Version.Path + "/collections/" + model.CollectionId + "/values/" + model.Key);
        }};
    }

    @JsonProperty("Key")
    public String getKey() {
        return this.key;
    }

    @JsonProperty("Data")
    public String getData() {
        return this.data;
    }

    @JsonProperty("Etag")
    public String getEtag() {
        return this.etag;
    }

    @JsonProperty("$metadata")
    public Dictionary<String, String> getMetadata() {
        return this.$metadata;
    }
}
