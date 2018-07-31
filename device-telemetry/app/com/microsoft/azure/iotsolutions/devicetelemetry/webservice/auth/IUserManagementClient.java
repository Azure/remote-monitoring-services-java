// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.auth;

import com.google.inject.ImplementedBy;

import java.util.List;
import java.util.concurrent.CompletionStage;

@ImplementedBy(UserManagementClient.class)
public interface IUserManagementClient {

    /**
     * Get a list of allowed actions based on current user's id and roles
     * @param userObjectId user's object id
     * @param roles user's current application role
     * @return allowed action list
     */
    CompletionStage<List<String>> getAllowedActions(String userObjectId, List<String> roles) throws ExternalDependencyException;

}