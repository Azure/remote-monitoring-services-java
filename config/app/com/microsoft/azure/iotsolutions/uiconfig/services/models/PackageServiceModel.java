// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

public class PackageServiceModel {
    private String id;
    private String name;
    private PackageType packageType;
    private String configType;
    private String content;
    private String dateCreated;

    public PackageServiceModel() {
    }

    public PackageServiceModel(
            String id,
            String name,
            PackageType packageType,
            String configType,
            String content) {
        this(id, name, packageType, configType, content, StringUtils.EMPTY);
    }

    public PackageServiceModel(
            String id,
            String name,
            PackageType packageType,
            String configType,
            String content,
            String dateCreated) {
        this.id = id;
        this.name = name;
        this.packageType = packageType;
        this.configType = configType;
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
    public PackageType getPackageType() { return this.packageType; }

    public void setPackageType(PackageType packageType) {
        this.packageType = packageType;
    }

    @JsonProperty("ConfigType")
    public String getConfigType() { return this.configType; }

    public void setConfigType(String configType) { this.configType = configType; }

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
