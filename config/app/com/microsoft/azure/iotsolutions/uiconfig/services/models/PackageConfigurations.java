package com.microsoft.azure.iotsolutions.uiconfig.services.models;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PackageConfigurations {

    private Set<String> customConfig = new HashSet<String>();

    public String[] configurations;

    public PackageConfigurations()
    {
        this.configurations = new String[0];
    }

    public String[] getConfigurations()
    {
        if (configurations != null)
        {
            customConfig.addAll(new HashSet<String>(Arrays.asList(configurations)));
        }
        return customConfig.toArray(new String[customConfig.size()]);
    }

    public void add(String customConfig)
    {
        customConfig = PackageConfigType.custom.toString() + " - " + customConfig;
        this.customConfig.add(customConfig.trim());
    }
}
