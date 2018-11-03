// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.ExternalDependencyException;

import java.util.concurrent.CompletionStage;

public class AzureResourceManagerClient implements IAzureResourceManagerClient {

    @Override
    public CompletionStage<Boolean> isOffice365EnabledAsync() throws ExternalDependencyException {
        return null;
    }
}
