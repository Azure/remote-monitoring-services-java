// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.AlarmRuleServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.Version;

import java.util.Dictionary;
import java.util.Hashtable;

public class AlarmRuleApiModel {
    private final String id;
    private final String description;

    public  AlarmRuleApiModel(
            final String id,
            final String description
    ) {
        this.id = id;
        this.description = description;
    }

    public AlarmRuleApiModel(final AlarmRuleServiceModel rule) {
        if(rule != null) {
            this.id = rule.getId();
            this.description = rule.getDescription();
        } else {
            this.id = null;
            this.description = null;
        }
    }

    @JsonProperty("Id")
    public String getId() { return  this.id; }

    @JsonProperty("Description")
    public String getDescription() { return  this.description; }

    @JsonProperty("$metadata")
    public Dictionary<String, String> getMetadata() {
        return new Hashtable<String, String>() {{
            put("$type", "Rule;" + Version.NAME);
            put("$uri", "/" + Version.NAME + "/rules/" + id);
        }};
    }
}
