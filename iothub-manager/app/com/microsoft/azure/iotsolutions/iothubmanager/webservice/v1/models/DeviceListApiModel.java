// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceServiceModel;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.Version;

import java.util.*;

public final class DeviceListApiModel {

    private final ArrayList<DeviceApiModel> items;

    public DeviceListApiModel(final ArrayList<DeviceServiceModel> devices) {

        this.items = new ArrayList<>();
        for (DeviceServiceModel device : devices) {
            this.items.add(new DeviceApiModel(device));
        }
    }

    @JsonProperty("Items")
    public ArrayList<DeviceApiModel> getItems() {
        return items;
    }

    @JsonProperty("$metadata")
    public Dictionary<String, String> getMetadata() {
        return new Hashtable<String, String>() {{
            put("$type", "DeviceList;" + Version.NAME);
            put("$uri", "/" + Version.NAME + "/devices");
        }};
    }
}
