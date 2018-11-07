// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.NotAuthorizedException;

import java.util.concurrent.CompletionStage;

@ImplementedBy(AzureResourceManagerClient.class)
public interface IAzureResourceManagerClient {

    /**
     *
     * @return
     * @throws ExternalDependencyException
     * @throws NotAuthorizedException
     */
    CompletionStage<Boolean> isOffice365EnabledAsync() throws ExternalDependencyException, NotAuthorizedException;
}
