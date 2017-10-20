// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.webservice.auth;

import com.google.inject.ImplementedBy;

@ImplementedBy(OpenIdConnectJwtValidation.class)
public interface IJwtValidation {
    Boolean validateToken(String token) throws InvalidConfigurationException, ExternalDependencyException;
}
