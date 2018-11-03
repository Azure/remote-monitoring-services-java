// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.ExternalDependencyException;

import java.util.concurrent.CompletionStage;

@ImplementedBy(AzureResourceManagerClient.class)
public interface IAzureResourceManagerClient {
    CompletionStage<Boolean> isOffice365EnabledAsync() throws ExternalDependencyException;
}
