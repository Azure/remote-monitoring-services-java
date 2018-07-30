// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.webservice.auth;

import com.google.inject.ImplementedBy;

import java.time.Duration;
import java.util.HashSet;

@ImplementedBy(ClientAuthConfig.class)
public interface IClientAuthConfig {

    // Whether the authentication and authorization is required or optional.
    // Default: true
    Boolean isAuthRequired();

    // Auth type: currently supports only "JWT"
    // Default: JWT
    String getAuthType();

    // The list of allowed signing algoritms
    // Default: RS256, RS384, RS512
    HashSet<String> getJwtAllowedAlgos();

    // The trusted issuer
    String getJwtIssuer();

    // The required audience
    String getJwtAudience();

    // Clock skew allowed when validating tokens expiration
    // Default: 2 minutes
    Duration getJwtClockSkew();
}
