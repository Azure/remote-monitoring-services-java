// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeploymentMetrics;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeploymentStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DeploymentMetricsApiModel {

    private static final String SUCCESSFUL_METRICS_KEY = "successfullCount";
    private static final String FAILED_METRICS_KEY = "failedCount";
    private static final String PENDING_METRICS_KEY = "pendingCount";

    private Map<String, Long> systemMetrics;
    private Map<String, Long> customMetrics;
    private Map<String, DeploymentStatus> deviceStatuses;

    public DeploymentMetricsApiModel() {
    }

    public DeploymentMetricsApiModel(DeploymentMetrics metricsServiceModel) {
        if (metricsServiceModel == null) return;

        this.customMetrics = metricsServiceModel.getSystemMetrics();
        this.systemMetrics = metricsServiceModel.getCustomMetrics();
        this.deviceStatuses = metricsServiceModel.getDeviceStatuses();

        if (metricsServiceModel.getDeviceMetrics() != null)
        {
            this.systemMetrics.put(SUCCESSFUL_METRICS_KEY,
                    metricsServiceModel.getDeviceMetrics().get(DeploymentStatus.Succeeded));
            this.systemMetrics.put(FAILED_METRICS_KEY,
                    metricsServiceModel.getDeviceMetrics().get(DeploymentStatus.Failed));
            this.systemMetrics.put(PENDING_METRICS_KEY,
                    metricsServiceModel.getDeviceMetrics().get(DeploymentStatus.Pending));
        }
    }

    @JsonProperty("SystemMetrics")
    public Map<String, Long> getSystemMetrics() {
        return this.systemMetrics;
    }

    @JsonProperty("CustomMetrics")
    public Map<String, Long> getCustomMetrics() {
        return this.customMetrics;
    }

    @JsonProperty("DeviceStatuses")
    public Map<String, DeploymentStatus> getDeviceStatuses() {
        return this.deviceStatuses;
    }
}
