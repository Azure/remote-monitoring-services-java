// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.storageadapter.services.models.ValueServiceModel;
import com.microsoft.azure.iotsolutions.storageadapter.webservice.v1.Version;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

public class ValueListApiModel {

    @JsonProperty("Items")
    private Iterator<ValueApiModel> Items;

    @JsonProperty("$metadata")
    private Hashtable<String, String> $metadata;

    public ValueListApiModel(Iterator<ValueServiceModel> models, String collectionId) {
        List<ValueApiModel> result = new ArrayList<>();
        while (models.hasNext()) {
            ValueServiceModel element = models.next();
            result.add(new ValueApiModel(element));
        }
        Items = result.iterator();

        $metadata = new Hashtable<String, String>() {{
            put("$type", "Status;" + Version.Number);
            put("$uri", "/" + Version.Path + "/collections/" + collectionId + "/values");
        }};
    }
}
