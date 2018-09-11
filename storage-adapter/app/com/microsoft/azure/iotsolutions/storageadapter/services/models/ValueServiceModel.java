// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.services.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.documentdb.Document;
import org.joda.time.DateTime;

public class ValueServiceModel {
    public String CollectionId;
    public String Key;
    public String Data;
    public String ETag;
    public DateTime Timestamp;


    public ValueServiceModel(String key, String data) {
        this.Key = key;
        this.Data = data;
        Timestamp = new DateTime();
    }

    //Http body json -> ValueServiceModel
    public ValueServiceModel(@JsonProperty("CollectionId") String collectionId, @JsonProperty("Key") String key, @JsonProperty("Data") String data, @JsonProperty("ETag") String etag) {
        this.CollectionId = collectionId;
        this.Key = key;
        this.Data = data;
        this.ETag = etag;
    }

    public ValueServiceModel(Document resource) {
        CollectionId = resource.getString("CollectionId");
        Key = resource.getString("Key");
        Data = resource.getString("Data");
        ETag = resource.getETag();
        // "_ts" is stored in units of Sec but here we need Millisec, 1 Sec == 1000 Millisec.
        Timestamp = new DateTime(resource.getTimestamp().getTime() * 1000);
    }

}
