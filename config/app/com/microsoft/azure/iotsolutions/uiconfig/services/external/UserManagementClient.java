// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.uiconfig.services.helpers.WsResponseHelper;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.IServicesConfig;
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
                    // Validate response
                    String message = String.format("Failed to get application token: %s", url);
                    WsResponseHelper.checkUnauthorizedStatus(response);
                    WsResponseHelper.checkError(error, message);
                    WsResponseHelper.checkSuccessStatusCode(response, message);

                    return Json.fromJson(response.asJson(), TokenApiModel.class).getAccessToken();
                });
    }
}
