// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.sdk.iot.service.ConfigurationMetrics;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.Map;

public class DeploymentMetrics {
    private Map<String, Long> metrics;
    private Map<String, DeploymentStatus> deviceStatuses;

    public DeploymentMetrics(ConfigurationMetrics systemMetrics,
                             ConfigurationMetrics customMetrics) {
        this.metrics = new HashMap<>();
        this.addConfigurationMetrics(systemMetrics);
        this.addConfigurationMetrics(customMetrics);
    }

    @JsonProperty("Metrics")
    public Map<String, Long> getMetrics() {
        return this.metrics;
    }

    @JsonProperty("DeviceStatuses")
    public Map<String, DeploymentStatus> getDeviceStatuses() { return this.deviceStatuses; }

    public void setDeviceStatuses(Map<String, DeploymentStatus> deviceStatuses) {
        this.deviceStatuses = deviceStatuses;
    }

    private void addConfigurationMetrics(ConfigurationMetrics metricsToAdd) {
        if (metricsToAdd != null && MapUtils.isNotEmpty(metricsToAdd.getResults())) {
            for (Map.Entry<String, Long> entry : metricsToAdd.getResults().entrySet()) {
                this.metrics.put(entry.getKey(), entry.getValue());
            }
        }
    }
}
