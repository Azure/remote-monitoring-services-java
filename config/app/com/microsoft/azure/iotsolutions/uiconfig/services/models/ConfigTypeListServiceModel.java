// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ConfigTypeListServiceModel {

    private Set<String> customConfigs = new HashSet<String>();

    @JsonProperty("configtypes")
    public String[] configTypes;

    public ConfigTypeListServiceModel()
    {
        this.configTypes = new String[0];
    }

    public String[] getConfigTypes() {
        if (configTypes != null) {
            customConfigs.addAll(new HashSet<String>(Arrays.asList(configTypes)));
        }
        return customConfigs.toArray(new String[customConfigs.size()]);
    }

    public void add(String customConfig)
    {
        this.customConfigs.add(customConfig.trim());
    }
}
