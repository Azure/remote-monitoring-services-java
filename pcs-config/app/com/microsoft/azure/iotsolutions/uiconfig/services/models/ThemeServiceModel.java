// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ThemeServiceModel {
    private String name;
    private String description;
    public static final ThemeServiceModel Default = new ThemeServiceModel();

    static {
        Default.name = "My Solution";
        Default.description = "My Solution Description";
    }

    @JsonProperty("Name")
    public String getName() {
        return name;
    }

    @JsonProperty("Description")
    public String getDescription() {
        return description;
    }
}
