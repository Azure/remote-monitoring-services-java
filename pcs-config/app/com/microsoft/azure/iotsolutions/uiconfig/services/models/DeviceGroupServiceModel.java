// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeviceGroupServiceModel {

    private String id;
    private String displayName;
    private Object conditions;
    private String eTag;

    public DeviceGroupServiceModel() {
    }

    public DeviceGroupServiceModel(String id, String displayName, Object conditions, String eTag) {
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
    public Object getConditions() {
        return conditions;
    }

    public void setConditions(Object conditions) {
        this.conditions = conditions;
    }

    @JsonProperty("ETag(")
    public String getETag() {
        return eTag;
    }

    public void setETag(String eTag) {
        this.eTag = eTag;
    }
}
