// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceServiceListModel;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceServiceModel;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.Version;

import java.util.*;

public final class DeviceListApiModel {

    private final List<DeviceRegistryApiModel> items;
    private String ContinuationToken;

    public DeviceListApiModel(final ArrayList<DeviceServiceModel> devices) {

        this.items = new ArrayList<>();
        for (DeviceServiceModel device : devices) {
            this.items.add(new DeviceRegistryApiModel(device));
        }
    }

    public DeviceListApiModel(final DeviceServiceListModel devices) {
        this.items = new ArrayList<DeviceRegistryApiModel>();
        this.ContinuationToken = devices.getContinuationToken();
        for (DeviceServiceModel d : devices.getItems()) {
            this.items.add(new DeviceRegistryApiModel(d));
        }
    }

    @JsonProperty("items")
    public List<DeviceRegistryApiModel> getItems() {
        return items;
    }

    @JsonProperty("$metadata")
    public Dictionary<String, String> getMetadata() {
        return new Hashtable<String, String>() {{
            put("$type", "DeviceList;" + Version.NUMBER);
            put("$uri", "/" + Version.PATH + "/devices");
        }};
    }
}
