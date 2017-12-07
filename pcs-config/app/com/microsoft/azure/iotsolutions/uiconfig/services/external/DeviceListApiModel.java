// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.uiconfig.services.helpers.HashSetHelper;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.DeviceTwinName;

import java.util.HashSet;
import java.util.List;

public class DeviceListApiModel {

    private List<DeviceRegistryApiModel> items;

    @JsonProperty("Items")
    public List<DeviceRegistryApiModel> getItems() {
        return items;
    }

    public void setItems(List<DeviceRegistryApiModel> items) {
        this.items = items;
    }

    public DeviceTwinName GetDeviceTwinNames() {
        HashSet<String> tagSet = new HashSet<>();
        HashSet<String> reportedSet = new HashSet<>();
        this.items.stream().forEach(m -> {
            m.getTags().entrySet().forEach(n -> {
                HashSetHelper.preparePropNames(tagSet, n.getValue(), n.getKey());
            });
            m.getProperties().getReported().entrySet().forEach(n -> {
                HashSetHelper.preparePropNames(reportedSet, n.getValue(), n.getKey());
            });
        });
        return new DeviceTwinName(tagSet, reportedSet);
    }
}
