// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum JobStatus {
    unknown(0),
    enqueued(1),
    running(2),
    completed(3),
    failed(4),
    cancelled(5),
    scheduled(6),
    queued(7);

    private final int value;

    JobStatus(int value) {
        this.value = value;
    }

    @JsonValue
    final int value() {
        return this.value;
    }

    /**
     * Convert ordinal or value to JobStatus
     *
     * @param ordinal an internal int value
     *
     * @return JobStatus
     */
    public static JobStatus from(int ordinal) {
        try {
            return values()[ordinal];
        } catch (Exception e) {
            throw new IllegalArgumentException("JobStatus", e);
        }
    }

    public static JobStatus fromAzureJobStatus(com.microsoft.azure.sdk.iot.service.jobs.JobStatus azureJobStatus) {
        return JobStatus.valueOf(azureJobStatus.toString());
    }

    public static com.microsoft.azure.sdk.iot.service.jobs.JobStatus toAzureJobStatus(JobStatus jobStatus) {
        if(jobStatus == null) throw new IllegalArgumentException("JobStatus");
        switch (jobStatus) {
            case enqueued:
                return com.microsoft.azure.sdk.iot.service.jobs.JobStatus.enqueued;
            case running:
                return com.microsoft.azure.sdk.iot.service.jobs.JobStatus.running;
            case completed:
                return com.microsoft.azure.sdk.iot.service.jobs.JobStatus.completed;
            case failed:
                return com.microsoft.azure.sdk.iot.service.jobs.JobStatus.failed;
            case cancelled:
                return com.microsoft.azure.sdk.iot.service.jobs.JobStatus.cancelled;
            case scheduled:
                return com.microsoft.azure.sdk.iot.service.jobs.JobStatus.scheduled;
            case queued:
                return com.microsoft.azure.sdk.iot.service.jobs.JobStatus.queued;
            default:
                return com.microsoft.azure.sdk.iot.service.jobs.JobStatus.unknown;
        }
    }
}
