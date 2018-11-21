package com.microsoft.azure.iotsolutions.uiconfig.services.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ConfigTypeList {

    private Set<String> customConfig = new HashSet<String>();

    @JsonProperty("configtypes")
    public String[] configTypes;

    public ConfigTypeList()
    {
        this.configTypes = new String[0];
    }

    public String[] getConfigurations()
    {
        if (configTypes != null)
        {
            customConfig.addAll(new HashSet<String>(Arrays.asList(configTypes)));
        }
        return customConfig.toArray(new String[customConfig.size()]);
    }

    public void add(String customConfig)
    {
        this.customConfig.add(customConfig.trim());
    }
}
