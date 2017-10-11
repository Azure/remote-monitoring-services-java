// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.List;

public class RuleApiModel {

    private String eTag;

    private String id;

    private String name;

    private boolean enabled;

    private String description;

    private String groupId;

    private String severity;

    private List<ConditionApiModel> conditions;

    @JsonProperty("DateCreated")
    public String getDateCreated() {
        return DateTime.now(DateTimeZone.UTC).toString("yyyy-MM-dd'T'HH:mm:ssZZ");
    }

    @JsonProperty("ETag")
    public String getETag() {
        return eTag;
    }

    public void setETag(String eTag) {
        this.eTag = eTag;
    }

    @JsonProperty("Id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("Name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("Enabled")
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @JsonProperty("Description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("GroupId")
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @JsonProperty("Severity")
    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    @JsonProperty("Conditions")
    public List<ConditionApiModel> getConditions() {
        return conditions;
    }

    public void setConditions(List<ConditionApiModel> conditions) {
        this.conditions = conditions;
    }
}