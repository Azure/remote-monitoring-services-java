// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.PackageServiceModel;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.PackageType;

public class PackageApiModel {

    private String id;
    private String name;
    private PackageType packageType;
    private String configType;
    private String dateCreated;
    private String content;

    public PackageApiModel(PackageServiceModel model) {
        this.id = model.getId();
        this.name = model.getName();
        this.packageType = model.getPackageType();
        this.configType = model.getConfigType();
        this.content = model.getContent();
        this.dateCreated = model.getDateCreated();
    }

    public PackageApiModel(
            String name,
            PackageType packageType,
            String config,
            String content) {
        this.name = name;
        this.packageType = packageType;
        this.configType = config;
        this.content = content;
    }

    public PackageServiceModel ToServiceModel() {
        return new PackageServiceModel(
                this.id,
                this.name,
                this.packageType,
                this.configType,
                this.content,
                this.dateCreated);
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

    @JsonProperty("PackageType")
    public PackageType getPackageType() { return this.packageType; }

    public void setPackageType(PackageType packageType) { this.packageType = packageType;}

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
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }
}
