// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.Package;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.PackageType;

public class PackageApiModel {

    private String id;
    private String name;
    private PackageType type;
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
        id = model.getId();
        name = model.getName();
        type = model.getType();
        content = model.getContent();
        dateCreated = model.getDateCreated();
    }

    public PackageApiModel(String name, PackageType type, String content) {
        this.name = name;
        this.type = type;
        this.content = content;
    }

    public Package ToServiceModel() {
        return new Package(this.id, this.name, this.type, this.content, this.dateCreated);
    }
}
