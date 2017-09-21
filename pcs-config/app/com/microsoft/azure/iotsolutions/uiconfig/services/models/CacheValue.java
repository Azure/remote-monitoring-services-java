// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;

public class CacheValue {

    private boolean rebuilding;
    private HashSet<String> tags;
    private HashSet<String> reported;

    @JsonProperty("Rebuilding")
    public boolean isRebuilding() {
        return rebuilding;
    }

    public void setRebuilding(boolean rebuilding) {
        this.rebuilding = rebuilding;
    }

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

    public CacheValue() {
    }

    public CacheValue(HashSet<String> tags, HashSet<String> reported, boolean rebuilding) {
        this.tags = tags;
        this.reported = reported;
        this.rebuilding = rebuilding;
    }

    public CacheValue(HashSet<String> tags, HashSet<String> reported) {
        this(tags, reported, false);
    }
}
