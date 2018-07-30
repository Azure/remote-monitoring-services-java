// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external;

import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.ResourceNotFoundException;

import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.List;

public interface IUserManagementClient {
    CompletionStage<List<String>> getAllowedActionsAsync(String userObjectId, List<String> roles)
            throws ResourceNotFoundException, CompletionException;
}
