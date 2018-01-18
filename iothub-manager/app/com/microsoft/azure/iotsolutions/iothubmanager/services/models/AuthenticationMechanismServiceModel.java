// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.models;

import com.microsoft.azure.sdk.iot.service.Device;
import com.microsoft.azure.sdk.iot.service.auth.*;

public class AuthenticationMechanismServiceModel {

    private String primaryKey;
    private String secondaryKey;
    private String primaryThumbprint;
    private String secondaryThumbprint;
    private AuthenticationType authenticationType;

    public AuthenticationMechanismServiceModel() {}

    public AuthenticationMechanismServiceModel(AuthenticationType authenticationType) {
        this(AuthenticationType.toAzureModel(authenticationType));
        this.authenticationType = authenticationType;
    }

    public AuthenticationMechanismServiceModel(com.microsoft.azure.sdk.iot.service.auth.AuthenticationType authenticationType) {
        this(new AuthenticationMechanism(authenticationType));
    }

    public AuthenticationMechanismServiceModel(AuthenticationMechanism azureModel) {
        switch (azureModel.getAuthenticationType()) {
            case SAS:
                this.primaryKey = azureModel.getSymmetricKey().getPrimaryKey();
                this.secondaryKey = azureModel.getSymmetricKey().getSecondaryKey();
                break;
            case SELF_SIGNED:
                this.authenticationType = AuthenticationType.SelfSinged;
                this.primaryThumbprint = azureModel.getPrimaryThumbprint();
                this.secondaryThumbprint = azureModel.getSecondaryThumbprint();
                break;
            case CERTIFICATE_AUTHORITY:
                this.authenticationType = AuthenticationType.CertificateAuthority;
                this.primaryThumbprint = azureModel.getPrimaryThumbprint();
                this.secondaryThumbprint = azureModel.getSecondaryThumbprint();
            default:
                throw new IllegalArgumentException("Not supported authentication type");
        }
    }

    public AuthenticationMechanismServiceModel(Device device) {
        switch (device.getAuthenticationType()) {
            case SAS:
                this.authenticationType = AuthenticationType.Sas;
                this.primaryKey = device.getSymmetricKey().getPrimaryKey();
                this.secondaryKey = device.getSymmetricKey().getSecondaryKey();
                break;
            case SELF_SIGNED:
                this.authenticationType = AuthenticationType.SelfSinged;
                this.primaryThumbprint = device.getPrimaryThumbprint();
                this.secondaryThumbprint = device.getSecondaryThumbprint();
                break;
            case CERTIFICATE_AUTHORITY:
                this.authenticationType = AuthenticationType.CertificateAuthority;
                this.primaryThumbprint = device.getPrimaryThumbprint();
                this.secondaryThumbprint = device.getSecondaryThumbprint();
            default:
                throw new IllegalArgumentException("Not supported authentication type");
        }
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getSecondaryKey() {
        return secondaryKey;
    }

    public void setSecondaryKey(String secondaryKey) {
        this.secondaryKey = secondaryKey;
    }

    public String getPrimaryThumbprint() {
        return primaryThumbprint;
    }

    public void setPrimaryThumbprint(String primaryThumbprint) {
        this.primaryThumbprint = primaryThumbprint;
    }

    public String getSecondaryThumbprint() {
        return secondaryThumbprint;
    }

    public void setSecondaryThumbprint(String secondaryThumbprint) {
        this.secondaryThumbprint = secondaryThumbprint;
    }

    public AuthenticationType getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(AuthenticationType authenticationType) {
        this.authenticationType = authenticationType;
    }

    public AuthenticationMechanism toAzureModel() {
        AuthenticationMechanism auth;

        switch (this.authenticationType) {
            case Sas:
                SymmetricKey key = new SymmetricKey();
                key.setPrimaryKey(this.primaryKey);
                key.setSecondaryKey(this.secondaryKey);
                auth = new AuthenticationMechanism(key);
                break;
            case SelfSinged:
                auth = new AuthenticationMechanism(this.primaryThumbprint, this.secondaryThumbprint);
                auth.setAuthenticationType(com.microsoft.azure.sdk.iot.service.auth.AuthenticationType.SELF_SIGNED);
                break;
            case CertificateAuthority:
                auth = new AuthenticationMechanism(this.primaryThumbprint, this.secondaryThumbprint);
                auth.setAuthenticationType(com.microsoft.azure.sdk.iot.service.auth.AuthenticationType.CERTIFICATE_AUTHORITY);
                break;
            default:
                throw new IllegalArgumentException("Not supported authentication type");
        }

        return auth;
    }
}


