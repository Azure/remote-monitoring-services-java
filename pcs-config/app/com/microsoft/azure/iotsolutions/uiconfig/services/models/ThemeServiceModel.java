// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.models;

public class ThemeServiceModel {
    private String name;
    private String description;
    public static final ThemeServiceModel Default = new ThemeServiceModel();

    static {
        Default.name = "My Solution";
        Default.description = "My Solution Description";
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
