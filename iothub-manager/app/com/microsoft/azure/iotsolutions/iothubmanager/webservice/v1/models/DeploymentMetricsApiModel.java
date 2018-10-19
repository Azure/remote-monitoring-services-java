// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeploymentMetrics;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeploymentStatus;

import java.util.Map;

public class DeploymentMetricsApiModel {
    private static final String APPLIED_METRICS_KEY = "appliedCount";
    private static final String TARGETED_METRICS_KEY = "targetedCount";
    private static final String SUCCESSFUL_METRICS_KEY = "reportedSuccessfulCount";
    private static final String FAILED_METRICS_KEY = "reportedFailedCount";

    private long appliedCount;
    private long failedCount;
    private long succeededCount;
    private long targetedCount;
    private Map<String, DeploymentStatus> deviceStatuses;

    public DeploymentMetricsApiModel() {
    }

    public DeploymentMetricsApiModel(DeploymentMetrics metricsServiceModel) {
        if (metricsServiceModel == null) return;

        Map<String, Long> metrics = metricsServiceModel.getMetrics();
        this.appliedCount = metrics.getOrDefault(APPLIED_METRICS_KEY,0L);
        this.targetedCount = metrics.getOrDefault(TARGETED_METRICS_KEY,0L);
        this.succeededCount = metrics.getOrDefault(SUCCESSFUL_METRICS_KEY,0L);
        this.failedCount = metrics.getOrDefault(FAILED_METRICS_KEY,0L);
        this.deviceStatuses = metricsServiceModel.getDeviceStatuses();
    }

    @JsonProperty("AppliedCount")
    public long getAppliedCount() {
        return this.appliedCount;
    }

    @JsonProperty("FailedCount")
    public long getFailedCount() {
        return this.failedCount;
    }

    @JsonProperty("SucceededCount")
    public long getSucceededCount() {
        return this.succeededCount;
    }

    @JsonProperty("TargetedCount")
    public long getTargetedCount() {
        return this.targetedCount;
    }

    @JsonProperty("DeviceStatuses")
    public Map<String, DeploymentStatus> getDeviceStatuses() {
        return this.deviceStatuses;
    }
}
