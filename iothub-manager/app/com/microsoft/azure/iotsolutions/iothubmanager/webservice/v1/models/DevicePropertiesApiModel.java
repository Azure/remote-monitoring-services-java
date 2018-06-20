// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.Version;

import java.util.Hashtable;
import java.util.TreeSet;

public class DevicePropertiesApiModel {
    private TreeSet<String> items;
    private Hashtable<String, String> metadata;

    @JsonProperty("Items")
    public TreeSet<String> getItems() {
        return items;
    }

    public void setItems(TreeSet<String> items) {
        this.items = items;
    }

    @JsonProperty("$metadata")
    public Hashtable<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Hashtable<String, String> metadata) {
        this.metadata = metadata;
    }

    public DevicePropertiesApiModel() {
    }

    public DevicePropertiesApiModel(TreeSet<String> model) {
        items = model;
        metadata = new Hashtable<String, String>();
        metadata.put("$type", String.format("DevicePropertyList;%s", Version.NUMBER));
        metadata.put("$url", String.format("/%s/deviceproperties", Version.PATH));
    }
}
