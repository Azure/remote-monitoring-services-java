// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.Package;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.PackageType;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.PackageConfigType;

public class PackageApiModel {

    private String id;
    private String name;
    private PackageType type;
    private PackageConfigType configType;
    private String dateCreated;
    private String content;

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
    public PackageType getType() {
        return this.type;
    }

    public void setType(PackageType type) {
        this.type = type;
    }

    @JsonProperty("Config")
    public PackageConfigType getPackageConfig() { return this.configType; }

    public void setPackageConfig(PackageConfigType configType) { this.configType = configType; }

    @JsonProperty("Content")
    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @JsonProperty("DateCreated")
    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public PackageApiModel(Package model) {
        this.id = model.getId();
        this.name = model.getName();
        this.type = model.getType();
        this.configType = model.getPackageConfig();
        this.content = model.getContent();
        this.dateCreated = model.getDateCreated();
    }

    public PackageApiModel(String name, PackageType type, PackageConfigType configType, String content) {
        this.name = name;
        this.type = type;
        this.configType = configType;
        this.content = content;
    }

    public Package ToServiceModel() {
        return new Package(this.id, this.name, this.type, this.configType, this.content, this.dateCreated);
    }
}
