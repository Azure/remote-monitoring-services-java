// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.auth;

import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AuthorizeAction extends Action<Authorize> {

    private final Function<Authorize, Authorizer> configurator;

    @Inject
    public AuthorizeAction(Authorizer authorizer) {
        this(Authorize -> authorizer);
    }

    public AuthorizeAction(Function<Authorize, Authorizer> configurator) {
        this.configurator = configurator;
    }

    /**
     * Check if current user has correct authorization to perform actions on the controller
     * with annotation @Authorize("...")
     * @param ctx Http context
     * @return result of next action
     */
    public CompletionStage<Result> call(final Http.Context ctx) {
        Authorizer Authorizer = configurator.apply(configuration);
        String allowedAction = configuration.value().toLowerCase().trim();

        // move forward if authentication is disabled or request come from internal services
        try {
            if (!Authorizer.isAuthRequired(ctx) || !Authorizer.isExternalRequest(ctx)) {
                return delegate.call(ctx);
            }
        } catch (Exception e) {
            Result internalServerError = internalServerError(Json.toJson(new HashMap<String, String>() {{
                put("Error", "Failed to get context of authorization from request");
            }}));
            return CompletableFuture.completedFuture(internalServerError);
        }

        // move forward if no action is required by the controller
        if (allowedAction == null || allowedAction.isEmpty()) {
            return delegate.call(ctx);
        }

        List<String> allowedActions;
        try {
            allowedActions = Authorizer.getAllowedActions(ctx);
        } catch (Exception e) {
            Result internalServerError = internalServerError(Json.toJson(new HashMap<String, String>() {{
                put("Error", "Failed to get allowed actions context from request");
            }}));
            return CompletableFuture.completedFuture(internalServerError);
        }

        List<String> lowerCasedActions = allowedActions.stream()
            .map(String::toLowerCase)
            .map(String::trim)
            .collect(Collectors.toList());

        // fail the request if current user doesn't have correct allowed actions
        if (lowerCasedActions == null || lowerCasedActions.size() == 0 || !lowerCasedActions.contains(allowedAction)) {
            Result unauthorized = Authorizer.onUnauthorized(ctx, allowedAction);
            return CompletableFuture.completedFuture(unauthorized);
        }

        return delegate.call(ctx);
    }
}