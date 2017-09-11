// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.models;

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

    public int getDeviceCount() {
        return deviceCount;
    }

    public void setDeviceCount(int deviceCount) {
        this.deviceCount = deviceCount;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }

    public int getSucceededCount() {
        return succeededCount;
    }

    public void setSucceededCount(int succeededCount) {
        this.succeededCount = succeededCount;
    }

    public int getRunningCount() {
        return runningCount;
    }

    public void setRunningCount(int runningCount) {
        this.runningCount = runningCount;
    }

    public int getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(int pendingCount) {
        this.pendingCount = pendingCount;
    }
}
