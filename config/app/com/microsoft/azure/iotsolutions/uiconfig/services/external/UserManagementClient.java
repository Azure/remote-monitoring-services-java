// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.NotAuthorizedException;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.IServicesConfig;
import org.apache.http.HttpStatus;
import play.Logger;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.mvc.Http;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;

public class UserManagementClient implements IUserManagementClient {

    private static final String DEFAULT_USER_ID = "default";
    private static final Logger.ALogger log = Logger.of(UserManagementClient.class);

    private final WSClient wsClient;
    private final String userManagementServiceUrl;

    @Inject
    public UserManagementClient(final IServicesConfig config, final WSClient wsClient) {
        this.wsClient = wsClient;
        this.userManagementServiceUrl = config.getUserManagementApiUrl();
    }

    @Override
    public CompletionStage<List<String>> getAllowedActionsAsync(String userObjectId, List<String> roles) {
        String url = String.format("%s/users/%s/allowedActions", this.userManagementServiceUrl, userObjectId);
        if (roles == null) {
            roles = new ArrayList<>();
        }

        return this.wsClient.url(url)
                .post(Json.toJson(roles))
                .handle((response, error) -> {
                    if (error != null) {
                        String message = String.format("Failed to get allowed actions: %s", url);
                        log.error(message, error.getCause());
                        throw new CompletionException(message, error.getCause());
                    } else if (response.getStatus() != Http.Status.OK) {
                        String message = String.format("Failed to get allowed actions: %s", url);
                        log.error(message);
                        throw new CompletionException(new ExternalDependencyException(message));
                    } else {
                        return Json.fromJson(response.asJson(), List.class);
                    }
                });
    }

    @Override
    public CompletionStage<String> getTokenAsync() throws CompletionException {
        String url = String.format("%s/users/%s/token", this.userManagementServiceUrl, DEFAULT_USER_ID);

        return this.wsClient.url(url)
                .get()
                .handle((response, error) -> {
                    if (error != null) {
                        // If the error is 403, the user who did the deployment is not authorized
                        // to assign the role for the application to have contributor access.
                        if (response.getStatus() == HttpStatus.SC_FORBIDDEN ||
                                response.getStatus() == HttpStatus.SC_UNAUTHORIZED) {
                            String message = String.format("The application is not authorized and has not been " +
                                    "assigned Contributor permissions for the subscription. Go to the Azure portal and " +
                                    "assign the application as a Contributor in order to retrieve the token. %s", url);
                            log.error(message, error.getCause());
                            throw new CompletionException(new NotAuthorizedException(message));
                        } else {
                            String message = String.format("Failed to get application token: %s", url);
                            log.error(message, error.getCause());
                            throw new CompletionException(new ExternalDependencyException(message, error.getCause()));
                        }
                    } else if (response.getStatus() != Http.Status.OK) {
                        String message = String.format("Failed to get application token: %s", url);
                        log.error(message);
                        throw new CompletionException(new ExternalDependencyException(message));
                    } else {
                        return Json.fromJson(response.asJson(), TokenApiModel.class).getAccessToken();
                    }
                });
    }
}
