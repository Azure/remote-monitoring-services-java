// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.NotAuthorizedException;

import java.util.concurrent.CompletionStage;

@ImplementedBy(AzureResourceManagerClient.class)
public interface IAzureResourceManagerClient {

    /**
     * Checks to see if the Office 365 Logic App Connector is configured properly
     * with a sender email address. If it is set up, then this method returns true.
     * If there is an issue with getting a token to make the request, the method will
     * throw a NotAuthorizedException.
     *
     * @throws ExternalDependencyException
     * @throws NotAuthorizedException
     */
    CompletionStage<Boolean> isOffice365EnabledAsync() throws ExternalDependencyException, NotAuthorizedException;
}
