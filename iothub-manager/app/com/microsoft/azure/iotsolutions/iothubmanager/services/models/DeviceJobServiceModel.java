// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.models;

import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.sdk.iot.service.jobs.JobResult;

import java.util.Date;

public class DeviceJobServiceModel {

    private String deviceId;
    private DeviceJobStatus status;
    private Date startTimeUtc;
    private Date endTimeUtc;
    private Date createdDateTimeUtc;
    private Date lastUpdatedDateTimeUtc;
    private MethodResultServiceModel outcome;
    private DeviceJobErrorServiceModel error;

    public DeviceJobServiceModel(JobResult deviceJob) throws ExternalDependencyException {
        this.deviceId = deviceJob.getDeviceId();

        this.status = DeviceJobStatus.fromAzureJobStatus(deviceJob.getJobStatus());
        this.startTimeUtc = deviceJob.getStartTime();
        this.endTimeUtc = deviceJob.getEndTime();
        this.createdDateTimeUtc = deviceJob.getCreatedTime();
        this.lastUpdatedDateTimeUtc = deviceJob.getLastUpdatedDateTime();

        if(deviceJob.getOutcomeResult() != null) {
            this.outcome = new MethodResultServiceModel(deviceJob.getOutcomeResult());
        }

        if(deviceJob.getError() != null) {
            this.error = new DeviceJobErrorServiceModel(deviceJob.getError());
        }
    }

    public String getDeviceId() {
        return deviceId;
    }

    public DeviceJobStatus getStatus() {
        return status;
    }

    public Date getStartTimeUtc() {
        return startTimeUtc;
    }

    public Date getEndTimeUtc() {
        return endTimeUtc;
    }

    public Date getCreatedDateTimeUtc() {
        return createdDateTimeUtc;
    }

    public Date getLastUpdatedDateTimeUtc() {
        return lastUpdatedDateTimeUtc;
    }

    public MethodResultServiceModel getOutcome() {
        return outcome;
    }

    public DeviceJobErrorServiceModel getError() {
        return error;
    }
}
