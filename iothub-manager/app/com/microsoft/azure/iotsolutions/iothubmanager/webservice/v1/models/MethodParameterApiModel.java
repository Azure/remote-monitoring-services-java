// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.MethodParameterServiceModel;
import org.joda.time.DateTime;

import java.time.Duration;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MethodParameterApiModel {
    private String name = null;
    private Duration responseTimeout = null;
    private Duration connectionTimeout = null;
    private String jsonPayload = null;

    public MethodParameterApiModel() {
    }

    public MethodParameterApiModel(MethodParameterServiceModel serviceModel) {
        this.name = serviceModel.getName();
        this.responseTimeout = serviceModel.getResponseTimeout();
        this.connectionTimeout = serviceModel.getConnectionTimeout();
        this.jsonPayload= serviceModel.getJsonPayload();
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    @JsonProperty("responseTimeout")
    public Duration getResponseTimeout(){
        return this.responseTimeout;
    }

    public void setResponseTimeout(Duration value) {
        this.responseTimeout = value;
    }

    @JsonProperty("connectionTimeout")
    public Duration getConnectionTimeout(){
        return this.connectionTimeout;
    }

    public void setConnectionTimeout(Duration value) {
        this.connectionTimeout = value;
    }

    @JsonProperty("jsonPayload")
    public String getJsonPayload(){
        return this.jsonPayload;
    }

    public void setJsonPayload(String value) {
        this.jsonPayload = value;
    }

    public MethodParameterServiceModel toServiceModel() {
        MethodParameterServiceModel serviceModel = new MethodParameterServiceModel();
        serviceModel.setName(this.name);
        serviceModel.setResponseTimeout(this.responseTimeout);
        serviceModel.setConnectionTimeout(this.connectionTimeout);
        serviceModel.setJsonPayload(this.jsonPayload);
        return serviceModel;
    }
}
