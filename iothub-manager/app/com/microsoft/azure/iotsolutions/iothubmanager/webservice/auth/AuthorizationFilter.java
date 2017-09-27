// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.auth;

import akka.stream.Materializer;
import com.google.inject.Inject;
import play.Logger;
import play.http.HttpEntity;
import play.mvc.*;
import play.twirl.api.Content;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

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

    @Inject
    public AuthorizationFilter(
        Materializer mat,
        IClientAuthConfig config,
        IJwtValidation jwtValidation) {
        super(mat);
        this.authRequired = config.isAuthRequired();
        this.jwtValidation = jwtValidation;
    }

    @Override
    public CompletionStage<Result> apply(
        Function<Http.RequestHeader, CompletionStage<Result>> nextFilter,
        Http.RequestHeader requestHeader) {

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
            return nextFilter.apply(requestHeader).thenApply(result -> result);
        }

        // Validate the authorization header
        Boolean authorized = false;
        Optional<String> authHeader = requestHeader.header(AUTH_HEADER);
        if (authHeader.isPresent()) {
            try {
                authorized = this.validateHeader(authHeader.get());
            } catch (Exception e) {
                log.error("Authorization header validation failed", e);
            }
        } else {
            log.error(AUTH_HEADER + " header not found");
        }

        // If not authorized, stop the request here
        if (!authorized) {
            log.warn("The request is not authorized");
            return CompletableFuture.completedFuture(unauthorizedErrorResponse());
        }

        // Proceed with the request
        log.debug("The authorization was succesful");
        return nextFilter.apply(requestHeader).thenApply(result -> result);
    }

    /**
     * Validate the JWT token in the authorization header
     */
    private Boolean validateHeader(String s) {

        if (!s.startsWith(AUTH_HEADER_PREFIX)) {
            log.error(AUTH_HEADER + " header prefix not found");
            return false;
        }

        String token = s.substring(AUTH_HEADER_PREFIX.length());

        return this.jwtValidation.validateToken(token);
    }

    /**
     * Return a 401 HTTP Response
     */
    private Result unauthorizedErrorResponse() {
        return new Result(401, errorResponsePayload());
    }

    private HttpEntity errorResponsePayload() {
        return HttpEntity.fromContent(new Error401(), "utf-8");
    }

    // TODO: there must be a simpler way to return a JSON object...
    private class Error401 implements Content {

        @Override
        public String body() {
            return "{\"Error\":\"Authentication required\"}";
        }

        @Override
        public String contentType() {
            return "application/json";
        }
    }
}
