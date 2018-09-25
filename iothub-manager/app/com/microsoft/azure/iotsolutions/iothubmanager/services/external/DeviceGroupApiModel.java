// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.external;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeviceGroupApiModel {

    private String id;
    private String displayName;
    private Iterable<DeviceGroupConditionApiModel> conditions;
    private String eTag;

    public DeviceGroupApiModel() {}

    public DeviceGroupApiModel(String id, String displayName, Iterable<DeviceGroupConditionApiModel> conditions, String eTag) {
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
    public Iterable<DeviceGroupConditionApiModel> getConditions() {
        return conditions;
    }

    public void setConditions(Iterable<DeviceGroupConditionApiModel> conditions) {
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
