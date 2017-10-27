// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DeviceJobStatus {
    pending(0),
    scheduled(1),
    running(2),
    completed(3),
    failed(4),
    cancelled(5);

    private final int value;

    DeviceJobStatus(int value) {
        this.value = value;
    }

    @JsonValue
    final int value() {
        return this.value;
    }

    public static DeviceJobStatus fromAzureJobStatus(com.microsoft.azure.sdk.iot.service.jobs.JobStatus azureJobStatus) {
        // 'pending' is not defined in Java SDK, use 'enqueued' as workaround
        if (azureJobStatus == com.microsoft.azure.sdk.iot.service.jobs.JobStatus.enqueued) {
            return DeviceJobStatus.pending;
        }
        return DeviceJobStatus.valueOf(azureJobStatus.toString());
    }

    public static DeviceJobStatus from(int ordinal) {
        try {
            return values()[ordinal];
        } catch (Exception e) {
            throw new IllegalArgumentException("JobStatus", e);
        }
    }
}
