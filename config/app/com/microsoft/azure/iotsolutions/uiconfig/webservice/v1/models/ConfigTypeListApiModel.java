package com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.ConfigType;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.ConfigTypeList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigTypeListApiModel {

    @JsonProperty("configurations")
    public String[] packageConfigurations;

    public String[] getPackageConfigurations()
    {
        return packageConfigurations;
    }

    public ConfigTypeListApiModel(ConfigTypeList configTypeList)
    {
        List<String> customConfigs = new ArrayList<String>(Arrays.asList(configTypeList.configurations));

        for(ConfigType type : ConfigType.values())
        {
            if (!(type.equals(ConfigType.custom)))
            {
                customConfigs.add(0,type.toString());
            }
        }

        this.packageConfigurations = customConfigs.toArray(new String[customConfigs.size()]);
    }

    public ConfigTypeListApiModel()
    {
        this.packageConfigurations = new String[0];
    }
}
