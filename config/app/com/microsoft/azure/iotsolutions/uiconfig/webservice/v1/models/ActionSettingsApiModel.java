// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.actions.ActionType;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.actions.IActionSettings;

import java.util.Map;
import java.util.TreeMap;

public class ActionSettingsApiModel {

    private String type;
    private Map<String, Object> settings;

    @JsonProperty("Type")
    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("Settings")
    public Map<String, Object> getSettings() {
        return this.settings;
    }

    public void setSettings(Map settings) {
        this.settings = settings;
    }

    public ActionSettingsApiModel() {}

    public ActionSettingsApiModel(IActionSettings actionSettings) {
        this.type = actionSettings.getType().name();
        this.settings = new TreeMap<String, Object>(actionSettings.getSettings());
    }
}
