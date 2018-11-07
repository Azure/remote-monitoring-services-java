// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external;

import com.google.inject.ImplementedBy;

import java.util.List;
import java.util.concurrent.CompletionStage;

@ImplementedBy(UserManagementClient.class)
public interface IUserManagementClient {

    /**
     * Get a list of allowed actions based on current user's id and roles
     *
     * @param userObjectId user's object id
     * @param roles        user's current application role
     * @return allowed action list
     */
    CompletionStage<List<String>> getAllowedActionsAsync(String userObjectId, List<String> roles);

    /**
     * Get the application access token with the default audience
     * in order to call the Azure management APIs.
     *
     * @return application access token
     */
    CompletionStage<String> getTokenAsync();
}
