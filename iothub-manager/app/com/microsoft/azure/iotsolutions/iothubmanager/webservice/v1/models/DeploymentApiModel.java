// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.ConfigType;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeploymentServiceModel;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeploymentType;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceGroup;

public class DeploymentApiModel {
    private String id;
    private String name;
    private String createdDateTimeUtc;
    private String deviceGroupId;
    private String deviceGroupName;
    private String deviceGroupQuery;
    private String packageContent;
    private String packageName;
    private int priority;
    private DeploymentType deploymentType;
    private ConfigType configType;
    private DeploymentMetricsApiModel metrics;

    public DeploymentApiModel() {}

    public DeploymentApiModel(String deploymentName, String deviceGroupId, String deviceGroupName,
                              String deviceGroupQuery, String packageContent, String packageName,
                              int priority, DeploymentType deploymentType, ConfigType configType) {
        this.name = deploymentName;
        this.deviceGroupId = deviceGroupId;
        this.deviceGroupName = deviceGroupName;
        this.deviceGroupQuery = deviceGroupQuery;
        this.packageContent = packageContent;
        this.packageName = packageName;
        this.priority = priority;
        this.deploymentType = deploymentType;
        this.configType = configType;
    }

    public DeploymentApiModel(DeploymentServiceModel serviceModel) {
        this.createdDateTimeUtc = serviceModel.getCreatedDateTimeUtc();
        this.id = serviceModel.getId();
        this.deviceGroupId = serviceModel.getDeviceGroup().getId();
        this.deviceGroupName = serviceModel.getDeviceGroup().getName();
        this.deviceGroupQuery = serviceModel.getDeviceGroup().getQuery();
        this.name = serviceModel.getName();
        this.packageContent = serviceModel.getPackageContent();
        this.packageName = serviceModel.getPackageName();
        this.priority = serviceModel.getPriority();
        this.deploymentType = serviceModel.getDeploymentType();
        this.configType = serviceModel.getConfigType();
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

    @JsonProperty("DeviceGroupName")
    public String getDeviceGroupName() { return this.deviceGroupName; }

    @JsonProperty("DeviceGroupQuery")
    public String getDeviceGroupQuery() {
        return this.deviceGroupQuery;
    }

    @JsonProperty("PackageContent")
    public String getPackageContent() {
        return this.packageContent;
    }

    @JsonProperty("PackageName")
    public String getPackageName() { return this.packageName; }

    @JsonProperty("Priority")
    public int getPriority() {
        return this.priority;
    }

    @JsonProperty("Type")
    public DeploymentType getDeploymentType() {
        return this.deploymentType;
    }

    @JsonProperty("ConfigType")
    public DeploymentType getConfigType() {
        return this.deploymentType;
    }

    @JsonProperty("Metrics")
    public DeploymentMetricsApiModel getMetrics() {
        return this.metrics;
    }

    public DeploymentServiceModel toServiceModel() {
        return new DeploymentServiceModel(this.name,
                new DeviceGroup(this.deviceGroupId, this.deviceGroupName, this.deviceGroupQuery),
                this.packageContent,
                this.packageName,
                this.priority,
                this.deploymentType,
                this.configType);
    }
}
