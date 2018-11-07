// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.InvalidConfigurationException;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.NotAuthorizedException;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.IActionsConfig;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.IServicesConfig;
import play.Logger;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.mvc.Http;

import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

public class AzureResourceManagerClient implements IAzureResourceManagerClient {

    private final WSClient wsClient;
    private static final Logger.ALogger log = Logger.of(AzureResourceManagerClient.class);
    private final String office365LogicAppUrl;
    private final String subscriptionId;
    private final String resourceGroup;
    private final String armEndpointUrl;
    private final String managementApiVersion;

    private IUserManagementClient userManagementClient;

    @Inject
    public AzureResourceManagerClient(final IServicesConfig config,
                                      final WSClient wsClient,
                                      IUserManagementClient userManagementClient) {
        this.wsClient = wsClient;
        this.userManagementClient = userManagementClient;

        IActionsConfig actionsConfig = config.getActionsConfig();
        this.office365LogicAppUrl = actionsConfig.getOffice365LogicAppUrl();
        this.subscriptionId = actionsConfig.getSubscriptionId();
        this.resourceGroup = actionsConfig.getResourceGroup();
        this.armEndpointUrl = actionsConfig.getArmEndpointUrl();
        this.managementApiVersion = actionsConfig.getManagementApiVersion();

        if (this.isNullOrEmpty(this.subscriptionId) ||
                this.isNullOrEmpty(this.resourceGroup) ||
                this.isNullOrEmpty(this.armEndpointUrl)) {

            throw new CompletionException(new InvalidConfigurationException("Subscription Id, " +
                    "Resource Group, and Arm Endpoint Url must be specified" +
                    "in the environment variable configuration for this " +
                    "solution in order to use this API."));
        }
    }

    @Override
    public CompletionStage<Boolean> isOffice365EnabledAsync() throws ExternalDependencyException, NotAuthorizedException {
        String logicAppTestConnectionUri = this.armEndpointUrl +
                String.format("subscriptions/%s/resourceGroups/%s" +
                                "/providers/Microsoft.Web/connections/" +
                                "office365-connector/extensions/proxy/" +
                                "testconnection?api-version=%s",
                        this.subscriptionId,
                        this.resourceGroup,
                        this.managementApiVersion);

        return this.createRequest(logicAppTestConnectionUri)
                .get()
                .handle((response, error) -> {
                    if (response.getStatus() != Http.Status.OK) {
                        log.debug(String.format("Office365 Logic App Connector is not set up: %s", error.getMessage()));
                        return false;
                    }

                    return true;
                });
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    /**
     * Prepares a WSRequest to the Azure management APIs with an application token
     * Uses the default audience from the User Management service. If the User Management
     * service returns a 403 Not Authorized exception, then the user deploying the application
     * did not have owner permissions in order to assign the application owner permission to access
     * the Azure Management APIs.
     *
     * @param url
     * @return WSRequest for the Azure Management service
     * @throws ExternalDependencyException
     * @throws NotAuthorizedException
     */
    private WSRequest createRequest(String url) throws ExternalDependencyException, NotAuthorizedException {
        WSRequest request = this.wsClient.url(url);

        try {
            String token = this.userManagementClient.getTokenAsync().toCompletableFuture().get().toString();
            request.addHeader("Authorization", "Bearer " + token);

        } catch (InterruptedException | ExecutionException e) {
            String message = "Unable to get application token.";
            log.error(message);
            throw new ExternalDependencyException(message);
        } catch (CompletionException e) {
            if (e.getCause().getClass() == NotAuthorizedException.class) {
                String message = "Unable to get application token. The application is not authorized " +
                        "and has not been assigned owner permissions for the subscription.";
                throw new NotAuthorizedException(message);
            }
        }
        return request;
    }
}
