// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.DeviceModelRef;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.RuleApiModel;

public class Template {

    private Iterable<DeviceModelRef> deviceModels;
    private Iterable<DeviceGroup> groups;
    private Iterable<RuleApiModel> rules;

    @JsonProperty("Groups")
    public Iterable<DeviceGroup> getGroups() {
        return groups;
    }

    public void setGroups(Iterable<DeviceGroup> groups) {
        this.groups = groups;
    }

    @JsonProperty("Rules")
    public Iterable<RuleApiModel> getRules() {
        return rules;
    }

    public void setRules(Iterable<RuleApiModel> rules) {
        this.rules = rules;
    }

    @JsonProperty("DeviceModels")
    public Iterable<DeviceModelRef> getDeviceModels() {
        return deviceModels;
    }

    public void setDeviceModels(Iterable<DeviceModelRef> deviceModels) {
        this.deviceModels = deviceModels;
    }
}
