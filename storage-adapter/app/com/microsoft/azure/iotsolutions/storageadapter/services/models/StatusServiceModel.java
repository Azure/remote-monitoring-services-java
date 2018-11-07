// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.services.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Hashtable;

public class StatusServiceModel {
    private StatusResultServiceModel status;
    private Hashtable<String, String> properties;
    private Hashtable<String, StatusResultServiceModel> dependencies;

    public StatusServiceModel(boolean isHealthy, String message) {
        this.status = new StatusResultServiceModel(isHealthy, message);
        this.properties = new Hashtable<String, String>();
        this.dependencies = new Hashtable<String, StatusResultServiceModel>();
    }

    public StatusResultServiceModel getStatus() {
        return this.status;
    }

    @JsonProperty("Status")
    public void setStatus(StatusResultServiceModel resultServiceModel) {
        this.status = resultServiceModel;
    }

    public Hashtable<String, String> getProperties() {
        return this.properties;
    }

    @JsonProperty("Properties")
    public void setProperties(Hashtable<String, String> properties) {
        this.properties = properties;
    }

    public void addProperty(String propertyName, String propertyValue) {
        this.properties.put(propertyName, propertyValue);
    }

    public Hashtable<String, StatusResultServiceModel> getDependencies() {
        return this.dependencies;
    }

    @JsonProperty("Dependencies")
    public void setDependencies(Hashtable<String, StatusResultServiceModel> dependencies) {
        this.dependencies = dependencies;
    }

    public void addDependency(String serviceName, StatusResultServiceModel status) {
        this.dependencies.put(serviceName, status);
    }
}
