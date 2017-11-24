// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models;

import com.fasterxml.jackson.annotation.*;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.*;

import java.util.*;

public class JobApiModel {

    private String jobId;
    private String queryCondition;
    private Date createdTimeUtc;
    private Date startTimeUtc;
    private Date endTimeUtc;
    private Long maxExecutionTimeInSeconds;
    private JobType type;
    private JobStatus status;
    private MethodParameterApiModel methodParameter;
    private DeviceTwinServiceModel updateTwin;
    private String failureReason;
    private String statusMessage;
    private JobStatistics resultStatistics;
    private List<DeviceJobApiModel> devices;

    private final String dateFormatString = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public JobApiModel() {}

    public JobApiModel(JobServiceModel serviceModel) {
        if (serviceModel != null) {
            this.jobId = serviceModel.getJobId();
            this.queryCondition = serviceModel.getQueryCondition();
            this.createdTimeUtc = serviceModel.getCreatedTimeUtc();
            this.startTimeUtc = serviceModel.getStartTimeUtc();
            this.endTimeUtc = serviceModel.getEndTimeUtc();
            this.maxExecutionTimeInSeconds = serviceModel.getMaxExecutionTimeInSeconds();
            this.type = serviceModel.getJobType();
            this.status = serviceModel.getJobStatus();
            this.methodParameter = serviceModel.getMethodParameter() == null ?
                null : new MethodParameterApiModel(serviceModel.getMethodParameter());
            this.updateTwin = serviceModel.getUpdateTwin();
            this.failureReason = serviceModel.getFailureReason();
            this.statusMessage = serviceModel.getStatusMessage();
            this.resultStatistics = serviceModel.getResultStatistics();
            List<DeviceJobServiceModel> deviceJobModels = serviceModel.getDevices();
            if(deviceJobModels == null) {
                this.devices = null;
            } else {
                this.devices = new ArrayList<>();
                deviceJobModels.forEach(job -> this.devices.add(new DeviceJobApiModel(job)));
            }
        }
    }

    public JobServiceModel toServiceModel() {
        JobServiceModel serviceModel = new JobServiceModel();
        serviceModel.setJobId(this.jobId);
        serviceModel.setQueryCondition(this.queryCondition);
        serviceModel.setCreatedTimeUtc(this.createdTimeUtc);
        serviceModel.setEndTimeUtc(this.endTimeUtc);
        serviceModel.setMaxExecutionTimeInSeconds(this.getMaxExecutionTimeInSeconds());
        serviceModel.setJobType(this.getType());
        serviceModel.setJobStatus(this.getStatus());
        serviceModel.setMethodParameter(this.getMethodParameter().toServiceModel());
        serviceModel.setUpdateTwin(this.getUpdateTwin());
        serviceModel.setFailureReason(this.getFailureReason());
        serviceModel.setStatusMessage(this.getStatusMessage());
        serviceModel.setResultStatistics(this.getResultStatistics());
        return serviceModel;
    }

    @JsonProperty("jobId")
    public String getJobId() {
        return jobId;
    }

    @JsonProperty("JobId")
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    @JsonProperty("queryCondition")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getQueryCondition() {
        return queryCondition;
    }

    @JsonProperty("QueryCondition")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public void setQueryCondition(String queryCondition) {
        this.queryCondition = queryCondition;
    }

    @JsonProperty("createdTimeUtc")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = dateFormatString)
    public Date getCreatedTimeUtc() {
        return createdTimeUtc;
    }

    @JsonProperty("CreatedTimeUtc")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public void setCreatedTimeUtc(Date createdTimeUtc) {
        this.createdTimeUtc = createdTimeUtc;
    }

    @JsonProperty("startTimeUtc")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = dateFormatString)
    public Date getStartTimeUtc() {
        return startTimeUtc;
    }

    @JsonProperty("StartTimeUtc")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public void setStartTimeUtc(Date startTimeUtc) {
        this.startTimeUtc = startTimeUtc;
    }

    @JsonProperty("endTimeUtc")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = dateFormatString)
    public Date getEndTimeUtc() {
        return endTimeUtc;
    }

    @JsonProperty("EndTimeUtc")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public void setEndTimeUtc(Date endTimeUtc) {
        this.endTimeUtc = endTimeUtc;
    }

    @JsonProperty("maxExecutionTimeInSeconds")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Long getMaxExecutionTimeInSeconds() {
        return maxExecutionTimeInSeconds;
    }

    @JsonProperty("MaxExecutionTimeInSeconds")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public void setMaxExecutionTimeInSeconds(Long maxExecutionTimeInSeconds) {
        this.maxExecutionTimeInSeconds = maxExecutionTimeInSeconds;
    }

    @JsonProperty("type")
    public JobType getType() {
        return type;
    }

    @JsonProperty("Type")
    public void setType(JobType type) {
        this.type = type;
    }

    @JsonProperty("status")
    public JobStatus getStatus() {
        return status;
    }

    @JsonProperty("Status")
    public void setStatus(JobStatus status) {
        this.status = status;
    }

    @JsonProperty("methodParameter")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public MethodParameterApiModel getMethodParameter() {
        return methodParameter;
    }

    @JsonProperty("MethodParameter")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public void setMethodParameter(MethodParameterApiModel methodParameter) {
        this.methodParameter = methodParameter;
    }

    @JsonProperty("updateTwin")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public DeviceTwinServiceModel getUpdateTwin() {
        return updateTwin;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public void setUpdateTwin(DeviceTwinServiceModel updateTwin) {
        this.updateTwin = updateTwin;
    }

    @JsonProperty("failureReason")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    @JsonProperty("statusMessage")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    @JsonProperty("resultStatistics")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public JobStatistics getResultStatistics() {
        return resultStatistics;
    }

    public void setResultStatistics(JobStatistics resultStatistics) {
        this.resultStatistics = resultStatistics;
    }

    @JsonProperty("Devices")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<DeviceJobApiModel> getDevices() {
        return devices;
    }

    public void setDevices(List<DeviceJobApiModel> devices) {
        this.devices = devices;
    }
}
