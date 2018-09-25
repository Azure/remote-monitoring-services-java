// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.external;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PackageApiModel {

    private String id;
    private String name;
    private PackageType type;
    private String dateCreated;
    private String content;

    public PackageApiModel() {}

    public PackageApiModel(String id, String name, PackageType type, String content) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.content = content;
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
}
