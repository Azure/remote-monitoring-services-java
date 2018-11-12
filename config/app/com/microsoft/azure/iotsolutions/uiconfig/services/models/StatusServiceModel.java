// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.models;

import com.fasterxml.jackson.annotation.*;

import java.util.ArrayList;
import java.util.Hashtable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StatusServiceModel {
    private StatusResultServiceModel status;
    private Hashtable<String, String> properties;
    private Hashtable<String, StatusResultServiceModel> dependencies;

    public StatusServiceModel(boolean isHealthy, String message) {
        this.status = new StatusResultServiceModel(isHealthy, message);
        this.properties = new Hashtable<>();
        this.dependencies = new Hashtable<>();
    }

    @JsonCreator
    public StatusServiceModel(
        @JsonProperty("Status") StatusResultServiceModel status,
        @JsonProperty("Properties") Hashtable<String, String> properties,
        @JsonProperty("Dependencies") Hashtable<String, StatusResultServiceModel> dependencies) {
        this.status = status;
        this.properties = properties;
        this.dependencies = dependencies;
    }

    public void setServiceStatus(
        String dependencyName,
        StatusResultServiceModel serviceResult,
        ArrayList<String> errors) {
        if (!serviceResult.getIsHealthy()) {
            errors.add(dependencyName + " check failed");
            this.status.setIsHealthy(false);
        }

        this.dependencies.put(dependencyName, serviceResult);
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
