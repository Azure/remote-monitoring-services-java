// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.IAzureResourceManagerClient;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.actions.EmailActionSettings;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.actions.IActionSettings;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.IServicesConfig;
import play.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class Actions implements IActions {

    private static final Logger.ALogger log = Logger.of(Actions.class);

    private final IAzureResourceManagerClient azureResourceManagerClient;
    private final IServicesConfig config;

    @Inject
    public Actions(IAzureResourceManagerClient azureResourceManagerClient,
                   IServicesConfig config) {
        this.azureResourceManagerClient = azureResourceManagerClient;
        this.config = config;
    }

    @Override
    public CompletionStage<List<IActionSettings>> getListAsync() throws ExternalDependencyException {
        ArrayList<IActionSettings> result = new ArrayList<>();

        // Add Email Action Settings
        EmailActionSettings emailSettings = new EmailActionSettings(
                this.azureResourceManagerClient,
                this.config);
        emailSettings.InitializeAsync();
        result.add(emailSettings);

        return CompletableFuture.completedFuture(result);
    }
}
