// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.webservice.auth;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.uiconfig.webservice.auth.exceptions.*;

@ImplementedBy(OpenIdConnectJwtValidation.class)
public interface IJwtValidation {
    Boolean validateToken(String token) throws InvalidConfigurationException, ExternalDependencyException;
    UserClaims getUserClaims(String token) throws NotAuthorizedException;
}
