package com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.ConfigType;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.ConfigTypeList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigTypeListApiModel {

    @JsonProperty("configurations")
    public String[] Items;

    public String[] getPackageConfigurations()
    {
        return Items;
    }

    public ConfigTypeListApiModel(ConfigTypeList configTypeList)
    {
        List<String> configTypes = Arrays.asList(configTypeList.configurations);

        for(ConfigType configType : ConfigType.values())
        {
            if (!(configType.equals(ConfigType.custom)))
            {
                configTypes.add(0, configType.toString());
            }
        }

        this.Items = configTypes.toArray(new String[configTypes.size()]);
    }

    public ConfigTypeListApiModel()
    {
        this.Items = new String[0];
    }
}
