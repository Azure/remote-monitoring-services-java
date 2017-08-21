// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.services.models;

import com.microsoft.azure.documentdb.Document;
import org.joda.time.DateTime;

public class ValueServiceModel {
    public String CollectionId;
    public String Key;
    public String Data;
    public String ETag;
    public DateTime Timestamp;

    public ValueServiceModel(String CollectionId, String key, String data) {
        this.CollectionId = CollectionId;
        this.Key = key;
        this.Data = data;
        this.Timestamp = DateTime.now();
    }

    public ValueServiceModel(Document resource) {
        CollectionId = resource.getString("CollectionId");
        Key = resource.getString("Key");
        Data = resource.getString("Data");
        ETag = resource.getString("ETag");

        //Todo: Verify timestamp, (TimeZone, Browser, Format, etc)
        String time = resource.getString("Timestamp");
        time = (time == null || time.isEmpty()) ? DateTime.now().toString() : time;
        Timestamp = DateTime.parse(time).toDateTimeISO();
    }


}
