// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeploymentMetrics;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeploymentStatus;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DeploymentMetricsApiModel {

    private static final String APPLIED_METRICS_KEY = "appliedCount";
    private static final String TARGETED_METRICS_KEY = "targetedCount";
    private static final String SUCCESEEDED_METRICS_KEY = "reportedSuccessfulCount";
    private static final String FAILED_METRICS_KEY = "reportedFailedCount";
    private static final String PENDING_METRICS_KEY = "pendingCount";

    private Map<String, Long> systemMetrics;
    private Map<String, Long> customMetrics;
    private Map<String, DeploymentStatus> deviceStatuses;

    public DeploymentMetricsApiModel() {}

    public DeploymentMetricsApiModel(DeploymentMetrics metricsServiceModel) {

        this.systemMetrics = new HashMap<String, Long>();

        if (this.systemMetrics.get(APPLIED_METRICS_KEY) != null) {
            this.systemMetrics.put(APPLIED_METRICS_KEY, this.systemMetrics.get(APPLIED_METRICS_KEY));
        }
        else {
            this.systemMetrics.put(APPLIED_METRICS_KEY, 0L);
        }

        if (this.systemMetrics.get(TARGETED_METRICS_KEY) != null) {
            this.systemMetrics.put(TARGETED_METRICS_KEY, this.systemMetrics.get(TARGETED_METRICS_KEY));
        }
        else {
            this.systemMetrics.put(TARGETED_METRICS_KEY, 0L);
        }

        if (metricsServiceModel == null) return;

        this.customMetrics = metricsServiceModel.getCustomMetrics();
        this.systemMetrics = MapUtils.isEmpty(metricsServiceModel.getSystemMetrics()) ?
                                            metricsServiceModel.getSystemMetrics() : this.systemMetrics;
        this.deviceStatuses = metricsServiceModel.getDeviceStatuses();

        if (metricsServiceModel.getDeviceMetrics() != null) {
            this.systemMetrics.put(SUCCESEEDED_METRICS_KEY,
                    metricsServiceModel.getDeviceMetrics().get(DeploymentStatus.Succeeded));
            this.systemMetrics.put(FAILED_METRICS_KEY,
                    metricsServiceModel.getDeviceMetrics().get(DeploymentStatus.Failed));
            this.systemMetrics.put(PENDING_METRICS_KEY,
                    metricsServiceModel.getDeviceMetrics().get(DeploymentStatus.Pending));
        }

        if (this.customMetrics != null) {
            // Override System metrics if custom metric contain same metrics
            if (this.customMetrics.containsKey(SUCCESEEDED_METRICS_KEY)) {
                this.systemMetrics.put(SUCCESEEDED_METRICS_KEY,
                        this.customMetrics.get(SUCCESEEDED_METRICS_KEY));
                this.customMetrics.remove(SUCCESEEDED_METRICS_KEY);
            }

            if (this.customMetrics.containsKey(FAILED_METRICS_KEY)) {
                this.systemMetrics.put(FAILED_METRICS_KEY,
                        this.customMetrics.get(FAILED_METRICS_KEY));
                this.customMetrics.remove(FAILED_METRICS_KEY);
            }

            if (this.customMetrics.containsKey(PENDING_METRICS_KEY)) {
                this.systemMetrics.put(PENDING_METRICS_KEY,
                        this.customMetrics.get(PENDING_METRICS_KEY));
                this.customMetrics.remove(PENDING_METRICS_KEY);
            }
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
