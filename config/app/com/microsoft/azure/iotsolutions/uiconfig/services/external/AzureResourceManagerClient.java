// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.InvalidConfigurationException;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.NotAuthorizedException;
import com.microsoft.azure.iotsolutions.uiconfig.services.helpers.WsResponseHelper;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.IActionsConfig;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.IServicesConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
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
        this.subscriptionId = actionsConfig.getSubscriptionId();
        this.resourceGroup = actionsConfig.getResourceGroup();
        this.armEndpointUrl = actionsConfig.getArmEndpointUrl();
        this.managementApiVersion = actionsConfig.getManagementApiVersion();

        if (StringUtils.isEmpty(this.subscriptionId.trim()) ||
                StringUtils.isEmpty(this.resourceGroup.trim()) ||
                StringUtils.isEmpty(this.armEndpointUrl.trim())) {
            throw new CompletionException(new InvalidConfigurationException("Subscription Id, " +
                    "Resource Group, and Arm Endpoint Url must be specified" +
                    "in the environment variable configuration for this " +
                    "solution in order to use this API."));
        }
    }

    @Override
    public CompletionStage<Boolean> isOffice365EnabledAsync() throws ExternalDependencyException, NotAuthorizedException {
        String logicAppTestConnectionUri = String.format("%ssubscriptions/%s/resourceGroups/%s" +
                        "/providers/Microsoft.Web/connections/" +
                        "office365-connector/extensions/proxy/" +
                        "testconnection?api-version=%s",
                this.armEndpointUrl,
                this.subscriptionId,
                this.resourceGroup,
                this.managementApiVersion);

        // Gets token from auth service and adds to header
        WSRequest request = this.createRequest(logicAppTestConnectionUri);
        return request.get()
                .handle((response, error) -> {
                    // If the call to testconnection fails with a 403, it means the application was not
                    // assigned the correct permissions to make the request. This can happen if the person doing
                    // the deployment is not an owner, or if there was an issue at deployment time.
                    if (response.getStatus() == HttpStatus.SC_FORBIDDEN) {
                        String message = String.format("The application is not authorized and has not been " +
                                "assigned owner permissions for the subscription. Go to the Azure portal and " +
                                "assign the application as an owner in order to retrieve the token.");
                        log.error(message);

                        throw new CompletionException(new NotAuthorizedException(message));
                    }

                    WsResponseHelper.checkError(error, "Failed to check status of Office365 Logic App Connector.");

                    // The testconnection call may return a 401, which means the user has not yet configured
                    // the O365 Logic App by signing in with the sender email address.
                    if (response.getStatus() != Http.Status.OK) {
                        log.debug(String.format("Office365 Logic App Connector is not set up."));
                        return false;
                    }

                    return true;
                });
    }

    /**
     * Prepares a WSRequest to the Azure management APIs with an application token
     * Uses the default audience from the User Management service. If the User Management
     * service returns a 401 or 403 unauthorized exception, then the user deploying the application
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
            if (e.getCause() instanceof NotAuthorizedException) {
                String message = "Unable to get application token. The application is not authorized " +
                        "and has not been assigned owner permissions for the subscription.";
                throw new NotAuthorizedException(message);
            }
        }
        return request;
    }
}
