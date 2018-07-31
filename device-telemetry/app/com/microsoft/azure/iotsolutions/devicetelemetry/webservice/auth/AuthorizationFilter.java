// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.auth;

import akka.stream.Materializer;
import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.auth.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.auth.exceptions.InvalidConfigurationException;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.auth.exceptions.NotAuthorizedException;
import play.Logger;
import play.libs.Json;
import play.mvc.Filter;
import play.mvc.Http;
import play.mvc.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import static play.mvc.Results.*;

public class AuthorizationFilter extends Filter {

    private static final Logger.ALogger log = Logger.of(AuthorizationFilter.class);

    // The authorization header carries a bearer token, with this prefix
    private static final String AUTH_HEADER_PREFIX = "Bearer ";

    // Usual authorization header, carrying the bearer token
    private static final String AUTH_HEADER = "Authorization";

    // User requests are marked with this header by the reverse proxy
    // TODO ~devis: this is a temporary solution for public preview only
    // TODO ~devis: remove this approach and use the service to service authentication
    // https://github.com/Azure/azure-iot-pcs-remote-monitoring-java/issues/8
    private static final String EXT_SOURCE_HEADER = "X-Source";

    private final IJwtValidation jwtValidation;

    private Boolean authRequired;

    private final IUserManagementClient userManagementClient;

    @Inject
    public AuthorizationFilter(
        Materializer mat,
        IClientAuthConfig config,
        IJwtValidation jwtValidation,
        IUserManagementClient userManagementClient) {
        super(mat);
        this.authRequired = config.isAuthRequired();
        this.jwtValidation = jwtValidation;
        this.userManagementClient = userManagementClient;
    }

    @Override
    public CompletionStage<Result> apply(
        Function<Http.RequestHeader, CompletionStage<Result>> nextFilter,
        Http.RequestHeader requestHeader) {

        requestHeader = requestHeader.addAttr(Authorizer.AUTH_REQUIRED_TYPED_KEY, this.authRequired);

        // If auth is disabled, proceed with the request
        if (!this.authRequired) {
            log.debug("Skipping auth (auth disabled)");
            return nextFilter.apply(requestHeader).thenApply(result -> result);
        }

        if (!requestHeader.header(EXT_SOURCE_HEADER).isPresent()) {
            // If the header is not present, it's a service to service
            // request running in the private network, so we skip the auth
            // required for user requests
            // Note: this is a temporary solution for public preview

            // Skip auth and proceed
            log.debug("Skipping auth for service to service request");
            requestHeader = requestHeader.addAttr(Authorizer.EXTERNAL_REQUEST_TYPED_KEY, false);
            return nextFilter.apply(requestHeader).thenApply(result -> result);
        }

        requestHeader = requestHeader.addAttr(Authorizer.EXTERNAL_REQUEST_TYPED_KEY, true);

        // Validate the authorization header
        Boolean authorized = false;
        Optional<String> authHeader = requestHeader.header(AUTH_HEADER);
        UserClaims userClaims;
        if (authHeader.isPresent()) {
            try {
                authorized = this.validateHeader(authHeader.get());
            } catch (ExternalDependencyException e) {
                log.error("Authorization header validation failed", e);
                return CompletableFuture.completedFuture(serviceTemporaryUnavailableResponse(e));
            } catch (Exception e) {
                log.error("Authorization header validation failed", e);
                return CompletableFuture.completedFuture(internalServerErrorResponse(e));
            }
        } else {
            log.error(AUTH_HEADER + " header not found");
        }

        // If not authorized, stop the request here
        if (!authorized) {
            log.warn("The request is not authorized");
            return CompletableFuture.completedFuture(unauthorizedResponse());
        }

        // Extract user id and role claims from token and use it to get allowed actions
        // from authentication service
        try {
            userClaims = this.getUserClaims(authHeader.get());
            List<String> allowedActions = this.userManagementClient
                    .getAllowedActions(userClaims.getUserObjectId(), userClaims.getRoles())
                    .toCompletableFuture()
                    .get();
            requestHeader = requestHeader.addAttr(Authorizer.ALLOWED_ACTIONS_TYPED_KEY, allowedActions);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(internalServerErrorResponse(e));
        }

        // Proceed with the request
        log.debug("The authorization was successful");
        return nextFilter.apply(requestHeader).thenApply(result -> result);
    }

    private UserClaims getUserClaims(String s) throws NotAuthorizedException {
        if (!s.startsWith(AUTH_HEADER_PREFIX)) {
            log.error(AUTH_HEADER + " header prefix not found");
            return null;
        }
        String token = s.substring(AUTH_HEADER_PREFIX.length());
        return this.jwtValidation.getUserClaims(token);
    }

    /**
     * Validate the JWT token in the authorization header
     */
    private Boolean validateHeader(String s)
        throws InvalidConfigurationException, ExternalDependencyException {

        if (!s.startsWith(AUTH_HEADER_PREFIX)) {
            log.error(AUTH_HEADER + " header prefix not found");
            return false;
        }

        String token = s.substring(AUTH_HEADER_PREFIX.length());

        return this.jwtValidation.validateToken(token);
    }

    private Result unauthorizedResponse() {

        return unauthorized(Json.toJson(new HashMap<String, String>() {{
            put("Error", "Authentication required");
        }}));
    }

    private Result serviceTemporaryUnavailableResponse(Exception e) {

        return status(Http.Status.SERVICE_UNAVAILABLE, Json.toJson(new HashMap<String, String>() {{
            put("Error", e.getMessage());
        }}));
    }

    private Result internalServerErrorResponse(Exception e) {

        return internalServerError(Json.toJson(new HashMap<String, String>() {{
            put("Error", e.getMessage());
        }}));
    }
}
