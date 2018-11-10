// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.models.actions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.uiconfig.services.Actions;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.NotAuthorizedException;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.IAzureResourceManagerClient;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.IServicesConfig;
import play.Logger;

import java.util.TreeMap;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

public class EmailActionSettings implements IActionSettings {

    private static final String IS_ENABLED_KEY = "IsEnabled";
    private static final String APP_PERMISSIONS_KEY = "ApplicationPermissionsAssigned";
    private static final String OFFICE365_CONNECTOR_URL_KEY = "Office365ConnectorUrl";

    private static final Logger.ALogger log = Logger.of(Actions.class);

    private final IAzureResourceManagerClient azureResourceManagerClient;
    private final IServicesConfig config;

    private ActionType type;
    private TreeMap<String, Object> settings;

    @Inject
    public EmailActionSettings(IAzureResourceManagerClient azureResourceManagerClient,
                               IServicesConfig config) {
        this.azureResourceManagerClient = azureResourceManagerClient;
        this.config = config;

        this.type = ActionType.Email;
        this.settings = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    public void Initialize() throws ExternalDependencyException {
        // Check sign-in status of Office 365 Logic App Connector
        boolean office365IsEnabled = false;
        boolean applicationPermissionsAssigned = true;

        try {
            office365IsEnabled = this.azureResourceManagerClient.isOffice365EnabledAsync().toCompletableFuture().get();
        } catch (InterruptedException | ExecutionException | CompletionException | NotAuthorizedException e) {
            // If there is a 403 Not Authorized exception, it means the application has not
            // been given owner permissions to make the isEnabled check. This can be configured
            // by an owner in the Azure Portal.
            if (e.getClass() == NotAuthorizedException.class || e.getCause().getClass() == NotAuthorizedException.class) {
                applicationPermissionsAssigned = false;
            } else {
                String message = "Unable to get email action settings.";
                log.error(message);
                throw new ExternalDependencyException(message, e);
            }
        }

        this.settings.put(IS_ENABLED_KEY, office365IsEnabled);
        // Get Url for Office 365 Logic App Connector setup in portal
        // for display on the web-ui for one-time setup.
        this.settings.put(APP_PERMISSIONS_KEY, applicationPermissionsAssigned);
        this.settings.put(OFFICE365_CONNECTOR_URL_KEY, this.config.getActionsConfig().getOffice365LogicAppUrl());

        this.log.debug("Email Action Settings Retrieved. Email setup status: " + office365IsEnabled, this.settings);
    }

    @Override
    @JsonProperty("Type")
    public ActionType getType() {
        return this.type;
    }

    @Override
    @JsonProperty("Settings ")
    public TreeMap getSettings() {
        return this.settings;
    }
}
