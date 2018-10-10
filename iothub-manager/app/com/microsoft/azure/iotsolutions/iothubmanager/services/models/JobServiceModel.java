// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.models;

import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.sdk.iot.service.jobs.*;

import java.util.*;

public class JobServiceModel {

    private String jobId;
    private String queryCondition;
    private Date createdTimeUtc;
    private Date startTimeUtc;
    private Date endTimeUtc;
    private Long maxExecutionTimeInSeconds;
    private JobType jobType;
    private JobStatus jobStatus;
    private MethodParameterServiceModel methodParameter;
    private DeviceTwinServiceModel updateTwin;
    private String failureReason;
    private String statusMessage;
    private JobStatistics resultStatistics;
    private List<DeviceJobServiceModel> devices;

    public JobServiceModel() {}

    public JobServiceModel(JobResult jobResult, List<JobResult> deviceJobs) throws ExternalDependencyException {
        this.jobId = jobResult.getJobId();
        this.queryCondition = jobResult.getQueryCondition();
        this.createdTimeUtc = jobResult.getCreatedTime();
        this.startTimeUtc = jobResult.getStartTime();
        this.endTimeUtc = jobResult.getEndTime();
        this.maxExecutionTimeInSeconds = jobResult.getMaxExecutionTimeInSeconds();
        this.jobType = JobType.fromAzureJobType(jobResult.getJobType());
        this.jobStatus = JobStatus.fromAzureJobStatus(jobResult.getJobStatus());

        if (jobResult.getCloudToDeviceMethod() != null) {
            this.methodParameter = new MethodParameterServiceModel(jobResult.getCloudToDeviceMethod());
        }

        if (jobResult.getUpdateTwin() != null) {
            this.updateTwin = new DeviceTwinServiceModel(jobResult.getUpdateTwin());
        }

        this.failureReason = jobResult.getFailureReason();
        this.statusMessage = jobResult.getStatusMessage();

        this.resultStatistics = new JobStatistics(jobResult.getJobStatistics());

        if (deviceJobs == null) {
            this.devices = null;
        } else {
            this.devices = new ArrayList<>();
            for(JobResult job : deviceJobs) {
                this.devices.add(new DeviceJobServiceModel(job));
            }
        }
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getQueryCondition() {
        return queryCondition;
    }

    public void setQueryCondition(String queryCondition) {
        this.queryCondition = queryCondition;
    }

    public Date getCreatedTimeUtc() {
        return createdTimeUtc;
    }

    public void setCreatedTimeUtc(Date createdTimeUtc) {
        this.createdTimeUtc = createdTimeUtc;
    }

    public Date getStartTimeUtc() {
        return startTimeUtc;
    }

    public void setStartTimeUtc(Date startTimeUtc) {
        this.startTimeUtc = startTimeUtc;
    }

    public Date getEndTimeUtc() {
        return endTimeUtc;
    }

    public void setEndTimeUtc(Date endTimeUtc) {
        this.endTimeUtc = endTimeUtc;
    }

    public Long getMaxExecutionTimeInSeconds() {
        return maxExecutionTimeInSeconds;
    }

    public void setMaxExecutionTimeInSeconds(Long maxExecutionTimeInSeconds) {
        this.maxExecutionTimeInSeconds = maxExecutionTimeInSeconds;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public JobStatus getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }

    public MethodParameterServiceModel getMethodParameter() {
        return methodParameter;
    }

    public void setMethodParameter(MethodParameterServiceModel methodParameter) {
        this.methodParameter = methodParameter;
    }

    public DeviceTwinServiceModel getUpdateTwin() {
        return updateTwin;
    }

    public void setUpdateTwin(DeviceTwinServiceModel updateTwin) {
        this.updateTwin = updateTwin;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public JobStatistics getResultStatistics() {
        return resultStatistics;
    }

    public void setResultStatistics(JobStatistics resultStatistics) {
        this.resultStatistics = resultStatistics;
    }

    public List<DeviceJobServiceModel> getDevices() {
        return devices;
    }
}
