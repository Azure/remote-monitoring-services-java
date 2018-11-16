// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.sdk.iot.service.ConfigurationMetrics;
import java.util.Map;

public class DeploymentMetrics {

    private Map<String, Long> systemMetrics;
    private Map<String, Long> customMetrics;
    private Map<String, DeploymentStatus> deviceStatuses;
    private Map<DeploymentStatus, Long> deviceMetrics;

    public DeploymentMetrics(ConfigurationMetrics systemMetrics,
                             ConfigurationMetrics customMetrics) {
        this.systemMetrics = systemMetrics != null ? systemMetrics.getResults() : null;
        this.customMetrics = customMetrics != null ? customMetrics.getResults() : null;
    }

    @JsonProperty("SystemMetrics")
    public Map<String, Long> getSystemMetrics() {
        return this.systemMetrics;
    }

    @JsonProperty("SystemMetrics")
    public Map<String, Long> getCustomMetrics() {
        return this.customMetrics;
    }


    @JsonProperty("DeviceStatuses")
    public Map<String, DeploymentStatus> getDeviceStatuses() { return this.deviceStatuses; }

    public Map<DeploymentStatus, Long> getDeviceMetrics() { return this.deviceMetrics; }

    public void setDeviceStatuses(Map<String, DeploymentStatus> deviceStatuses) {
        this.deviceStatuses = deviceStatuses;
    }

    public void setDeviceMetrics(Map<DeploymentStatus, Long> deviceMetrics) {
        this.deviceMetrics = deviceMetrics;
    }

}
