// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.RuleServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.Version;

import java.util.Dictionary;
import java.util.Hashtable;

public class AlarmRuleApiModel {
    private final String id;
    private final String severity;
    private final String description;

    public AlarmRuleApiModel(
        final String id,
        final String severity,
        final String description
    ) {
        this.id = id;
        this.severity = severity;
        this.description = description;
    }

    public AlarmRuleApiModel(final RuleServiceModel rule) {
        this.id = rule.getId();
        this.severity = rule.getSeverity();
        this.description = rule.getDescription();
    }

    @JsonProperty("Id")
    public String getId() {
        return this.id;
    }

    @JsonProperty("Severity")
    public String getSeverity() {
        return this.severity;
    }

    @JsonProperty("Description")
    public String getDescription() {
        return this.description;
    }

    @JsonProperty("$metadata")
    public Dictionary<String, String> getMetadata() {
        return new Hashtable<String, String>() {{
            put("$type", "Rule;" + Version.NAME);
            put("$uri", "/" + Version.NAME + "/rules/" + id);
        }};
    }
}
