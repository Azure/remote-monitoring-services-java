// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.auth;

import com.google.inject.Inject;
import play.Logger;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.mvc.Http;

import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;

public class UserManagementClient implements IUserManagementClient {

    private static final Logger.ALogger log = Logger.of(UserManagementClient.class);

    private final WSClient wsClient;
    private final String userManagementServiceUrl;

    @Inject
    public UserManagementClient(final IClientAuthConfig config, final WSClient wsClient) {
        this.userManagementServiceUrl = config.getAuthServiceUrl();
        this.wsClient = wsClient;
    }

    @Override
    public CompletionStage<List<String>> getAllowedActions(String userObjectId, List<String> roles) {
        String url = String.format("%s/users/%s/allowedActions", this.userManagementServiceUrl, userObjectId);
        return this.wsClient.url(url)
            .post(Json.toJson(roles))
            .handle((response, error) -> {
                if (error != null) {
                    String message = String.format("Fail to get allowed actions: %s", url);
                    log.error(message, error.getCause());
                    throw new CompletionException(message, error.getCause());
                } else if (response.getStatus() != Http.Status.OK) {
                    String message = String.format("Fail to get allowed actions: %s", url);
                    log.error(message);
                    throw new CompletionException(new ExternalDependencyException(message));
                } else {
                    return Json.fromJson(response.asJson(), List.class);
                }
            });
    }
}
