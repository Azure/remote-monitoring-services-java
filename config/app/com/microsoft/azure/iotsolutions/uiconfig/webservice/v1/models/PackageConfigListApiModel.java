package com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.PackageConfigType;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.PackageConfigurations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PackageConfigListApiModel {

    @JsonProperty("configurations")
    public String[] packageConfigurations;

    public String[] getPackageConfigurations()
    {
        return packageConfigurations;
    }

    public PackageConfigListApiModel(PackageConfigurations packageConfigurations)
    {
        List<String> customConfigs = new ArrayList<String>(Arrays.asList(packageConfigurations.configurations));

        for(PackageConfigType type : PackageConfigType.values())
        {
            if (!(type.equals(PackageConfigType.custom)))
            {
                customConfigs.add(0,type.toString());
            }
        }

        this.packageConfigurations = customConfigs.toArray(new String[customConfigs.size()]);
    }

    public PackageConfigListApiModel()
    {
        this.packageConfigurations = new String[0];
    }
}
