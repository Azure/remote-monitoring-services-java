// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.external;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;

public class DeviceGroupFiltersApiModel {

    private HashSet<String> tags;

    private HashSet<String> reported;

    @JsonProperty("Tags")
    public HashSet<String> getTags() {
        return tags;
    }

    public void setTags(HashSet<String> tags) {
        this.tags = tags;
    }

    @JsonProperty("Reported")
    public HashSet<String> getReported() {
        return reported;
    }

    public void setReported(HashSet<String> reported) {
        this.reported = reported;
    }
}
