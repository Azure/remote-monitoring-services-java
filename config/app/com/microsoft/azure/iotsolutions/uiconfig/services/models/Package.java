// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

public class Package {
    private String id;
    private String name;
    private PackageType type;
    private String config;
    private String content;
    private String dateCreated;

    public Package() {
    }

    public Package(
            String id,
            String name,
            PackageType type,
            String config,
            String content) {
        this(id, name, type, config, content, StringUtils.EMPTY);
    }

    public Package(
            String id,
            String name,
            PackageType type,
            String config,
            String content,
            String dateCreated) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.config = config;
        this.content = content;
        this.dateCreated = dateCreated;
    }

    @JsonProperty("Id")
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("Name")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("Type")
    public PackageType getType() { return this.type; }

    public void setType(PackageType type) {
        this.type = type;
    }

    @JsonProperty("Config")
    public String getConfig() { return this.config; }

    public void setConfig(String configType) { this.config = configType; }

    @JsonProperty("Content")
    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @JsonProperty("DateCreated")
    public String getDateCreated() {
        return this.dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }
}
