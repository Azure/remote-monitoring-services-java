// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.RuleServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.Version;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class RuleListApiModel {
    private final ArrayList<RuleApiModel> items;

    public RuleListApiModel(final List<RuleServiceModel> rules) {
        this.items = new ArrayList<>();
        if (rules != null) {
            for (RuleServiceModel rule : rules) {
                this.items.add(new RuleApiModel(rule));
            }
        }
    }

    @JsonProperty("Items")
    public ArrayList<RuleApiModel> getItems() {
        return this.items;
    }

    @JsonProperty("$metadata")
    public Dictionary<String, String> getMetadata() {
        return new Hashtable<String, String>() {{
            put("$type", "RuleList;" + Version.NAME);
            put("$uri", "/" + Version.NAME + "/rules");
        }};
    }
}
