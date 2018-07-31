// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.auth;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.auth.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.auth.exceptions.InvalidConfigurationException;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.auth.exceptions.NotAuthorizedException;

@ImplementedBy(OpenIdConnectJwtValidation.class)
public interface IJwtValidation {
    Boolean validateToken(String token) throws InvalidConfigurationException, ExternalDependencyException;

    UserClaims getUserClaims(String token) throws NotAuthorizedException;
}
