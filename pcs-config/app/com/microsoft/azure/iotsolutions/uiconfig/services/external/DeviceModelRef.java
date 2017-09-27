// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeviceModelRef {

    private String Id;

    private int Count;

    @JsonProperty("Id")
    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    @JsonProperty("Count")
    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }
}
