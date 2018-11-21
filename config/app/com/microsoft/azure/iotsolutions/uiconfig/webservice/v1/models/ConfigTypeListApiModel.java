package com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.ConfigType;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.ConfigTypeList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigTypeListApiModel {

    @JsonProperty("Items")
    public String[] configTypes;

    public String[] getConfigTypes()
    {
        return configTypes;
    }

    public ConfigTypeListApiModel(ConfigTypeList configTypeList)
    {
        this.configTypes = configTypeList.getConfigurations();
    }

    public ConfigTypeListApiModel()
    {
        this.configTypes = new String[0];
    }
}
