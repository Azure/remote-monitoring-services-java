// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.Package;
import com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.Version;

import java.util.Hashtable;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PackageListApiModel {

    private Iterable<PackageApiModel> items;
    private Hashtable<String, String> metadata;

    @JsonProperty("items")
    public Iterable<PackageApiModel> getItems() {
        return items;
    }

    public void setItems(Iterable<PackageApiModel> items) {
        this.items = items;
    }

    @JsonProperty("$metadata")
    public Hashtable<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Hashtable<String, String> metadata) {
        this.metadata = metadata;
    }

    public PackageListApiModel() {
    }

    public PackageListApiModel(Iterable<Package> models) {
        items = StreamSupport.stream(models.spliterator(), false)
                .map(m -> new PackageApiModel(m)).collect(Collectors.toList());
        metadata = new Hashtable<String, String>();
        metadata.put("$type", String.format("Package;%s", Version.Number));
        metadata.put("$url", String.format("/%s/packages", Version.Path));
    }
}