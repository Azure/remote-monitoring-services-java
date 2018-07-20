// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.AlarmServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.Version;

import java.util.*;

public class AlarmListApiModel {
    private final ArrayList<AlarmApiModel> items;

    public AlarmListApiModel(final ArrayList<AlarmServiceModel> alarms) {
        this.items = new ArrayList<>();
        if (alarms != null) {
            for (AlarmServiceModel alarm : alarms) {
                this.items.add(new AlarmApiModel(alarm));
            }
        }
    }

    @JsonProperty("Items")
    public ArrayList<AlarmApiModel> getItems() {
        return this.items;
    }

    @JsonProperty("$metadata")
    public Dictionary<String, String> getMetadata() {
        return new Hashtable<String, String>() {{
            put("$type", "Alarms;" + Version.NAME);
            put("$uri", "/" + Version.NAME + "/alarms");
        }};
    }
}
