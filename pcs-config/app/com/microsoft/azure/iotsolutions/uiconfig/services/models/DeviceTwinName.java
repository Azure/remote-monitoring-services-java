// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;

public class DeviceTwinName {

    private HashSet<String> tags;
    private HashSet<String> reportedProperties;

    public HashSet<String> getTags() {
        return tags;
    }

    public void setTags(HashSet<String> tags) {
        this.tags = tags;
    }

    public HashSet<String> getReportedProperties() {
        return reportedProperties;
    }

    public DeviceTwinName() {
    }

    public DeviceTwinName(HashSet<String> tags, HashSet<String> reportedProperties) {
        this.tags = tags;
        this.reportedProperties = reportedProperties;
    }

    public void setReportedProperties(HashSet<String> reportedProperties) {
        this.reportedProperties = reportedProperties;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return (this.tags == null || this.tags.isEmpty())
                && (this.reportedProperties == null || this.reportedProperties.isEmpty());
    }
}
