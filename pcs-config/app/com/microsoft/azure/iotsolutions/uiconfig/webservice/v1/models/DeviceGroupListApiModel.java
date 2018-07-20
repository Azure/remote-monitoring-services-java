// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.DeviceGroup;
import com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.Version;

import java.util.Hashtable;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class DeviceGroupListApiModel {

    private Iterable<DeviceGroupApiModel> items;
    private Hashtable<String, String> metadata;

    @JsonProperty("items")
    public Iterable<DeviceGroupApiModel> getItems() {
        return items;
    }

    public void setItems(Iterable<DeviceGroupApiModel> items) {
        this.items = items;
    }

    @JsonProperty("$metadata")
    public Hashtable<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Hashtable<String, String> metadata) {
        this.metadata = metadata;
    }

    public DeviceGroupListApiModel() {
    }

    public DeviceGroupListApiModel(Iterable<DeviceGroup> models) {
        items = StreamSupport.stream(models.spliterator(), false)
                .map(m -> new DeviceGroupApiModel(m)).collect(Collectors.toList());
        metadata = new Hashtable<String, String>();
        metadata.put("$type", String.format("DeviceGroup;%s", Version.Number));
        metadata.put("$url", String.format("/%s/devicegroups", Version.Path));
    }
}
