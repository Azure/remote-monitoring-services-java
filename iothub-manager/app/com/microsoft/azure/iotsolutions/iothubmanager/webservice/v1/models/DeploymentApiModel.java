// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeploymentServiceModel;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeploymentType;

public class DeploymentApiModel {
    private String id;
    private String name;
    private String createdDateTimeUtc;
    private String deviceGroupId;
    private String deviceGroupQuery;
    private String packageContent;
    private int priority;
    private DeploymentType type;
    private DeploymentMetricsApiModel metrics;

    public DeploymentApiModel() {}

    public DeploymentApiModel(String deploymentName, String deviceGroupId,
                              String deviceGroupQuery, String packageContent, int priority,
                              DeploymentType deploymentType) {
        this.name = deploymentName;
        this.deviceGroupId = deviceGroupId;
        this.deviceGroupQuery = deviceGroupQuery;
        this.packageContent = packageContent;
        this.priority = priority;
        this.type = deploymentType;
    }

    public DeploymentApiModel(DeploymentServiceModel serviceModel) {
        this.createdDateTimeUtc = serviceModel.getCreatedDateTimeUtc();
        this.id = serviceModel.getId();
        this.deviceGroupId = serviceModel.getDeviceGroupId();
        this.deviceGroupQuery = serviceModel.getDeviceGroupQuery();
        this.name = serviceModel.getName();
        this.packageContent = serviceModel.getPackageContent();
        this.priority = serviceModel.getPriority();
        this.type = serviceModel.getType();
        this.metrics = new DeploymentMetricsApiModel(serviceModel.getDeploymentMetrics());
    }

    @JsonProperty("Id")
    public String getId() {
        return this.id;
    }

    @JsonProperty("Name")
    public String getName() {
        return this.name;
    }

    @JsonProperty("CreatedDateTimeUtc")
    public String getCreatedDateTimeUtc() {
        return this.createdDateTimeUtc;
    }

    @JsonProperty("DeviceGroupId")
    public String getDeviceGroupId() {
        return this.deviceGroupId;
    }

    @JsonProperty("DeviceGroupQuery")
    public String getDeviceGroupQuery() {
        return this.deviceGroupQuery;
    }

    @JsonProperty("PackageContent")
    public String getPackageContent() {
        return this.packageContent;
    }

    @JsonProperty("Priority")
    public int getPriority() {
        return this.priority;
    }

    @JsonProperty("Type")
    public DeploymentType getType() {
        return this.type;
    }

    @JsonProperty("Metrics")
    public DeploymentMetricsApiModel getMetrics() {
        return this.metrics;
    }

    public DeploymentServiceModel toServiceModel() {
        return new DeploymentServiceModel(this.name,
                                          this.deviceGroupId,
                                          this.deviceGroupQuery,
                                          this.packageContent,
                                          this.priority,
                                          this.type);
    }
}
