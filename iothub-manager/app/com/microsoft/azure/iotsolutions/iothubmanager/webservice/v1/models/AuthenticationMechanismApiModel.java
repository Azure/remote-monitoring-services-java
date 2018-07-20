// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.AuthenticationMechanismServiceModel;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.AuthenticationType;

public class AuthenticationMechanismApiModel {

    private String primaryKey;
    private String secondaryKey;
    private String primaryThumbprint;
    private String secondaryThumbprint;
    private AuthenticationType authenticationType;

    public AuthenticationMechanismApiModel() {}

    public AuthenticationMechanismApiModel(AuthenticationMechanismServiceModel model) {
        if (model != null) {
            this.authenticationType = model.getAuthenticationType();
            this.primaryKey = model.getPrimaryKey();
            this.secondaryKey = model.getSecondaryKey();
            this.primaryThumbprint = model.getPrimaryThumbprint();
            this.secondaryThumbprint = model.getSecondaryThumbprint();
        }
    }

    @JsonProperty("PrimaryKey")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    @JsonProperty("SecondaryKey")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getSecondaryKey() {
        return secondaryKey;
    }

    public void setSecondaryKey(String secondaryKey) {
        this.secondaryKey = secondaryKey;
    }

    @JsonProperty("PrimaryThumbprint")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getPrimaryThumbprint() {
        return primaryThumbprint;
    }

    public void setPrimaryThumbprint(String primaryThumbprint) {
        this.primaryThumbprint = primaryThumbprint;
    }

    @JsonProperty("SecondaryThumbprint")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getSecondaryThumbprint() {
        return secondaryThumbprint;
    }

    public void setSecondaryThumbprint(String secondaryThumbprint) {
        this.secondaryThumbprint = secondaryThumbprint;
    }

    @JsonProperty("AuthenticationType")
    public AuthenticationType getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(AuthenticationType authenticationType) {
        this.authenticationType = authenticationType;
    }

    public AuthenticationMechanismServiceModel toServiceModel() {
        AuthenticationMechanismServiceModel serviceModel = this.authenticationType == null
            ? new AuthenticationMechanismServiceModel() : new AuthenticationMechanismServiceModel(this.authenticationType);
        serviceModel.setPrimaryKey(this.primaryKey);
        serviceModel.setSecondaryKey(this.secondaryKey);
        serviceModel.setPrimaryThumbprint(this.primaryThumbprint);
        serviceModel.setSecondaryThumbprint(this.secondaryThumbprint);
        return serviceModel;
    }
}
