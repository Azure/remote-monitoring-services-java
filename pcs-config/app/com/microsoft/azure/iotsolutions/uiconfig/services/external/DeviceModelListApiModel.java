// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.uiconfig.services.helpers.HashSetHelper;

import java.util.HashSet;
import java.util.List;
import java.util.stream.StreamSupport;

public class DeviceModelListApiModel {

    private List<DeviceModelApiModel> items;

    @JsonProperty("Items")
    public List<DeviceModelApiModel> getItems() {
        return items;
    }

    public void setItems(List<DeviceModelApiModel> items) {
        this.items = items;
    }

    public HashSet<String> GetPropNames() {
        HashSet<String> set = new HashSet<>();
        StreamSupport.stream(items.spliterator(), false).forEach(m -> {
            m.getProperties().entrySet().stream().forEach(n -> {
                HashSetHelper.preparePropNames(set, n.getValue(), n.getKey());
            });
        });
        return set;
    }
}
