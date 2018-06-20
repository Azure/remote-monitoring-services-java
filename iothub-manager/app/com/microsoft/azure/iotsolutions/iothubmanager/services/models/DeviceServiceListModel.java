// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.iothubmanager.services.helpers.HashMapHelper;

import java.util.HashSet;
import java.util.List;
import java.util.stream.StreamSupport;

public class DeviceServiceListModel {

    private String continuationToken;
    private List<DeviceServiceModel> items;

    public DeviceServiceListModel(List<DeviceServiceModel> devices, String continuationToken) {
        this.continuationToken = continuationToken;
        this.items = devices;
    }

    @JsonProperty("Items")
    public List<DeviceServiceModel> getItems() {
        return this.items;
    }

    public String getContinuationToken() {
        return this.continuationToken;
    }

    public DeviceTwinName toDeviceTwinNames() {
        HashSet<String> tagSet = new HashSet<>();
        StreamSupport.stream(items.spliterator(), false).forEach(m -> {
            HashSet<String> currentTagSet = HashMapHelper.mapToHashSet("", m.getTwin().getTags());
            tagSet.addAll(currentTagSet);
        });

        HashSet<String> reportedSet = new HashSet<>();
        StreamSupport.stream(items.spliterator(), false).forEach(m -> {
            HashSet<String> currentTagSet = HashMapHelper.mapToHashSet("", m.getTwin().getProperties().getReported());
            reportedSet.addAll(currentTagSet);
        });

        return new DeviceTwinName(tagSet, reportedSet);
    }
}
