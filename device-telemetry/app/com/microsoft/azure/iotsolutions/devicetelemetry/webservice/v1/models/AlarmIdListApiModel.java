// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class AlarmIdListApiModel {
    private final ArrayList<String> items;

    public AlarmIdListApiModel() {
        this.items = null;
    }

    public AlarmIdListApiModel(final ArrayList<String> items) {
        this.items = new ArrayList<>();
        if (items != null) {
            this.items.addAll(items);
        }
    }

    @JsonProperty("Items")
    public ArrayList<String> getItems() {
        return this.items;
    }
}