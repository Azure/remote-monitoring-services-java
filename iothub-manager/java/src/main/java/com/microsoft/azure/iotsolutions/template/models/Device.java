// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.template.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Device {

    final String name;
    final long id;

    @JsonCreator
    public Device(@JsonProperty("name") String name, @JsonProperty("id") long id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }
}
