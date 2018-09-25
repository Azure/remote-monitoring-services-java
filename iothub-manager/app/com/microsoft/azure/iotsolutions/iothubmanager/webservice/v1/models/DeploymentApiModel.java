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
    private String packageId;
    private int priority;
    private DeploymentType type;
    private DeploymentMetricsApiModel metrics;

    public DeploymentApiModel() {}

    public DeploymentApiModel(String deploymentName, String deviceGroupId,
                              String packageId, int priority,
                              DeploymentType deploymentType) {
        this.name = deploymentName;
        this.deviceGroupId = deviceGroupId;
        this.packageId = packageId;
        this.priority = priority;
        this.type = deploymentType;
    }

    public DeploymentApiModel(DeploymentServiceModel serviceModel) {
        this.createdDateTimeUtc = serviceModel.getCreatedDateTimeUtc();
        this.id = serviceModel.getId();
        this.deviceGroupId = serviceModel.getDeviceGroupId();
        this.name = serviceModel.getName();
        this.packageId = serviceModel.getPackageId();
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

    @JsonProperty("PackageId")
    public String getPackageId() {
        return this.packageId;
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
        return new DeploymentServiceModel(this.deviceGroupId,
                                          this.packageId,
                                          this.name,
                                          this.priority,
                                          this.type);
    }
}
