// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models;

import com.fasterxml.jackson.annotation.*;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceJobServiceModel;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceJobStatus;

import java.util.Date;

public class DeviceJobApiModel {

    private String deviceId;
    private DeviceJobStatus status;
    private Date startTimeUtc;
    private Date endTimeUtc;
    private Date createdDateTimeUtc;
    private Date lastUpdatedDateTimeUtc;
    private MethodResultApiModel outcome;
    private DeiceJobErrorApiModel error;

    private final String dateFormatString = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public DeviceJobApiModel(DeviceJobServiceModel serviceModel) {
        this.deviceId = serviceModel.getDeviceId();
        this.status = serviceModel.getStatus();
        this.startTimeUtc = serviceModel.getStartTimeUtc();
        this.endTimeUtc = serviceModel.getEndTimeUtc();
        this.createdDateTimeUtc = serviceModel.getCreatedDateTimeUtc();
        this.lastUpdatedDateTimeUtc = serviceModel.getLastUpdatedDateTimeUtc();

        if(serviceModel.getOutcome() != null){
            this.outcome = new MethodResultApiModel(serviceModel.getOutcome());
        }

        if(serviceModel.getError() != null) {
            this.error = new DeiceJobErrorApiModel(serviceModel.getError());
        }
    }

    @JsonProperty("DeviceId")
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @JsonProperty("Status")
    public DeviceJobStatus getStatus() {
        return status;
    }

    public void setStatus(DeviceJobStatus status) {
        this.status = status;
    }

    @JsonProperty("StartTimeUtc")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = dateFormatString)
    public Date getStartTimeUtc() {
        return startTimeUtc;
    }

    public void setStartTimeUtc(Date startTimeUtc) {
        this.startTimeUtc = startTimeUtc;
    }

    @JsonProperty("EndTimeUtc")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = dateFormatString)
    public Date getEndTimeUtc() {
        return endTimeUtc;
    }

    public void setEndTimeUtc(Date endTimeUtc) {
        this.endTimeUtc = endTimeUtc;
    }

    @JsonProperty("CreatedDateTimeUtc")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = dateFormatString)
    public Date getCreatedDateTimeUtc() {
        return createdDateTimeUtc;
    }

    public void setCreatedDateTimeUtc(Date createdDateTimeUtc) {
        this.createdDateTimeUtc = createdDateTimeUtc;
    }

    @JsonProperty("LastUpdatedDateTimeUtc")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = dateFormatString)
    public Date getLastUpdatedDateTimeUtc() {
        return lastUpdatedDateTimeUtc;
    }

    public void setLastUpdatedDateTimeUtc(Date lastUpdatedDateTimeUtc) {
        this.lastUpdatedDateTimeUtc = lastUpdatedDateTimeUtc;
    }

    @JsonProperty("Outcome")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public MethodResultApiModel getOutcome() {
        return outcome;
    }

    public void setOutcome(MethodResultApiModel outcome) {
        this.outcome = outcome;
    }

    @JsonProperty("Error")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public DeiceJobErrorApiModel getError() {
        return error;
    }

    public void setError(DeiceJobErrorApiModel error) {
        this.error = error;
    }
}
