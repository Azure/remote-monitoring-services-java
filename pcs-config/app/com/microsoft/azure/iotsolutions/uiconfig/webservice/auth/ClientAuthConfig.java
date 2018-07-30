// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.webservice.auth;

import java.time.Duration;
import java.util.HashSet;

public class ClientAuthConfig implements IClientAuthConfig {

    private Boolean authRequired;
    private String authType;
    private HashSet<String> jwtAllowedAlgos;
    private String jwtIssuer;
    private String jwtAudience;
    private Duration jwtClockSkew;

    public ClientAuthConfig(
        Boolean authRequired,
        String authType,
        HashSet<String> jwtAllowedAlgos,
        String jwtIssuer,
        String jwtAudience,
        Duration jwtClockSkew
    ) {
        this.authRequired = authRequired;
        this.authType = authType;
        this.jwtAllowedAlgos = jwtAllowedAlgos;
        this.jwtIssuer = jwtIssuer;
        this.jwtAudience = jwtAudience;
        this.jwtClockSkew = jwtClockSkew;
    }

    @Override
    public Boolean isAuthRequired() {
        return this.authRequired;
    }

    @Override
    public String getAuthType() {
        return this.authType;
    }

    @Override
    public HashSet<String> getJwtAllowedAlgos() {
        return this.jwtAllowedAlgos;
    }

    @Override
    public String getJwtIssuer() {
        return this.jwtIssuer;
    }

    @Override
    public String getJwtAudience() {
        return this.jwtAudience;
    }

    @Override
    public Duration getJwtClockSkew() {
        return this.jwtClockSkew;
    }
}
