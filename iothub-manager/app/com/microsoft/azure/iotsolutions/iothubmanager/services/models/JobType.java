// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum JobType {
    unknown(0),
    scheduleDeviceMethod(3),
    scheduleUpdateTwin(4);

    private int value;

    JobType(int value) {
        this.value = value;
    }

    @JsonValue
    final int value() {
        return this.value;
    }

    public static JobType from(int value) {
        for (JobType v : values()) {
            if (v.value == value) return v;
        }
        throw new IllegalArgumentException("JobType");
    }

    public static JobType fromAzureJobType(com.microsoft.azure.sdk.iot.service.jobs.JobType azureJobType) {
        return JobType.valueOf(azureJobType.toString());
    }

    public static com.microsoft.azure.sdk.iot.service.jobs.JobType toAzureJobType(JobType jobType) {
        if (jobType == null) throw new IllegalArgumentException("JobType");
        switch (jobType) {
            case scheduleDeviceMethod:
                return com.microsoft.azure.sdk.iot.service.jobs.JobType.scheduleDeviceMethod;
            case scheduleUpdateTwin:
                return com.microsoft.azure.sdk.iot.service.jobs.JobType.scheduleUpdateTwin;
            case unknown:
                return com.microsoft.azure.sdk.iot.service.jobs.JobType.unknown;
            default:
                throw new IllegalArgumentException("JobType");
        }
    }
}
