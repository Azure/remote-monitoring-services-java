// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.ConditionApiModel;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.DeviceGroup;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.DeviceGroupCondition;
import com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.Version;

import java.util.Hashtable;

public class DeviceGroupApiModel {

    private String id;
    private String displayName;
    private Iterable<DeviceGroupCondition> conditions;
    private String eTag;

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

    @JsonProperty("$metadata")
    public Hashtable<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Hashtable<String, String> metadata) {
        metadata = metadata;
    }

    public Hashtable<String, String> metadata;

    public DeviceGroupApiModel() {
    }

    public DeviceGroupApiModel(String id, String displayName, Iterable<DeviceGroupCondition> conditions, String eTag) {
        this.id = id;
        this.displayName = displayName;
        this.conditions = conditions;
        this.eTag = eTag;
    }

    public DeviceGroupApiModel(DeviceGroup model) {
        id = model.getId();
        displayName = model.getDisplayName();
        conditions = model.getConditions();
        eTag = model.getETag();
        metadata = new Hashtable<String, String>();
        metadata.put("$type", String.format("DeviceGroup;%s", Version.Number));
        metadata.put("$url", String.format("/%s/devicegroups/%s", Version.Path, model.getId()));
    }

    public DeviceGroup ToServiceModel() {
        DeviceGroup result = new DeviceGroup();
        result.setConditions(conditions);
        result.setDisplayName(displayName);
        return result;
    }
}
