// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.ConditionApiModel;

public class DeviceGroup {

    private String id;
    private String displayName;
    private Iterable<DeviceGroupCondition> conditions;
    private String eTag;

    public DeviceGroup() {
    }

    public DeviceGroup(String id, String displayName, Iterable<DeviceGroupCondition> conditions, String eTag) {
        this.id = id;
        this.displayName = displayName;
        this.conditions = conditions;
        this.eTag = eTag;
    }

    @JsonProperty("Id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("DisplayName")
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @JsonProperty("Conditions")
    public Iterable<DeviceGroupCondition> getConditions() {
        return conditions;
    }

    public void setConditions(Iterable<DeviceGroupCondition> conditions) {
        this.conditions = conditions;
    }

    @JsonProperty("ETag")
    public String getETag() {
        return eTag;
    }

    public void setETag(String eTag) {
        this.eTag = eTag;
    }
}
