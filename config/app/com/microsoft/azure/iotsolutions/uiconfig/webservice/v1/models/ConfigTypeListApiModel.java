// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.ConfigTypeListServiceModel;
import com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.Version;

import java.util.Hashtable;

public class ConfigTypeListApiModel {

    @JsonProperty("Items")
    public String[] configTypes;

    public Hashtable<String, String> metadata;

    public ConfigTypeListApiModel() {
        this.configTypes = new String[0];

        metadata = new Hashtable<String, String>();
        metadata.put("$type", String.format("ConfigTypes;%s", Version.Number));
        metadata.put("$url", String.format("/%s/configTypes/%s", Version.Path));
    }

    public ConfigTypeListApiModel(ConfigTypeListServiceModel configTypeList) {
        this.configTypes = configTypeList.getConfigTypes();
    }

    public String[] getConfigTypes() { return configTypes; }

    @JsonProperty("$metadata")
    public Hashtable<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Hashtable<String, String> metadata) {
        metadata = metadata;
    }

}
