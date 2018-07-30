// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JobStatistics {

    private int deviceCount;
    private int failedCount;
    private int succeededCount;
    private int runningCount;
    private int pendingCount;

    public JobStatistics() {}

    public JobStatistics(com.microsoft.azure.sdk.iot.service.jobs.JobStatistics azureModel) {
        if (azureModel != null) {
            this.deviceCount = azureModel.getDeviceCount();
            this.failedCount = azureModel.getFailedCount();
            this.succeededCount = azureModel.getSucceededCount();
            this.runningCount = azureModel.getRunningCount();
            this.pendingCount = azureModel.getPendingCount();
        }
    }

    @JsonProperty("DeviceCount")
    public int getDeviceCount() {
        return deviceCount;
    }

    public void setDeviceCount(int deviceCount) {
        this.deviceCount = deviceCount;
    }

    @JsonProperty("FailedCount")
    public int getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }

    @JsonProperty("SucceededCount")
    public int getSucceededCount() {
        return succeededCount;
    }

    public void setSucceededCount(int succeededCount) {
        this.succeededCount = succeededCount;
    }

    @JsonProperty("RunningCount")
    public int getRunningCount() {
        return runningCount;
    }

    public void setRunningCount(int runningCount) {
        this.runningCount = runningCount;
    }

    @JsonProperty("PendingCount")
    public int getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(int pendingCount) {
        this.pendingCount = pendingCount;
    }
}
