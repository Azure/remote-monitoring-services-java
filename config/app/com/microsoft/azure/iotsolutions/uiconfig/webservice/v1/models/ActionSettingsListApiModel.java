// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.actions.IActionSettings;
import com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.Version;

import java.util.ArrayList;
import java.util.Hashtable;

public class ActionSettingsListApiModel {

    private ArrayList<ActionSettingsApiModel> items;
    private Hashtable<String, String> metadata;

    public ActionSettingsListApiModel() {}

    public ActionSettingsListApiModel(Iterable<IActionSettings> actionSettingsList) {
        this.items = new ArrayList<>();
        for (IActionSettings item : actionSettingsList) {
            items.add(new ActionSettingsApiModel(item));
        }

        this.metadata = new Hashtable<>();
        this.metadata.put("$type", String.format("ActionSettingsList;%s", Version.Number));
        this.metadata.put("$url", String.format("/%s/solution-settings/actions", Version.Path));
    }

    @JsonProperty("Items")
    public Iterable<ActionSettingsApiModel> getItems() {
        return this.items;
    }

    @JsonProperty("$metadata")
    public Hashtable<String, String> getMetadata() {
        return this.metadata;
    }

    public void setMetadata(Hashtable<String, String> metadata) {
        this.metadata = metadata;
    }
}
