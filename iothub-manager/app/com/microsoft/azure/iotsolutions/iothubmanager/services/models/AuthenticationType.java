package com.microsoft.azure.iotsolutions.iothubmanager.services.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AuthenticationType {
    Sas(0),                     // Shared Access Key
    SelfSinged(1),              // Self-signed certificate
    CertificateAuthority(2);    // Certificate Authority

    private final int value;

    @JsonValue
    final int value() {
        return this.value;
    }

    AuthenticationType(int value) {
        this.value = value;
    }

    public static com.microsoft.azure.sdk.iot.service.auth.AuthenticationType toAzureModel(AuthenticationType authenticationType) {
        switch(authenticationType) {
            case Sas:
                return com.microsoft.azure.sdk.iot.service.auth.AuthenticationType.SAS;
            case SelfSinged:
                return com.microsoft.azure.sdk.iot.service.auth.AuthenticationType.SELF_SIGNED;
            case CertificateAuthority:
                return com.microsoft.azure.sdk.iot.service.auth.AuthenticationType.CERTIFICATE_AUTHORITY;
            default:
                throw new IllegalArgumentException("Not supported authentication type");
        }
    }
}

