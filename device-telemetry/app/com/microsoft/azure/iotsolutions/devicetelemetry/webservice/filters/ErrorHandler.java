// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.filters;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.*;
import com.typesafe.config.Config;
import play.Environment;
import play.api.OptionalSourceMapper;
import play.api.routing.Router;
import play.http.DefaultHttpErrorHandler;
import play.mvc.Http.RequestHeader;
import play.mvc.Http.Status;
import play.mvc.Result;
import play.mvc.Results;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;

import static play.libs.Json.toJson;

@Singleton
public class ErrorHandler extends DefaultHttpErrorHandler {

    private Config config;

    @Inject
    public ErrorHandler(Config config, Environment environment,
                        OptionalSourceMapper sourceMapper, Provider<Router> routes) {
        super(config, environment, sourceMapper, routes);
        this.config = config;
    }

    protected CompletionStage<Result> onForbidden(RequestHeader request, String message) {
        return CompletableFuture.completedFuture(
            Results.forbidden("You're not allowed to access this resource.")
        );
    }

    public CompletionStage<Result> onClientError(RequestHeader request, int statusCode, String message) {
        return CompletableFuture.completedFuture(
            Results.status(statusCode, "A client error occurred: " + message)
        );
    }

    public CompletionStage<Result> onServerError(RequestHeader request, Throwable e) {
        if (e instanceof CompletionException) {
            Throwable cause = e.getCause();
            if (cause instanceof ResourceNotFoundException) {
                return CompletableFuture.completedFuture(
                    Results.notFound(getErrorResponse(cause, true))
                );
            } else if (cause instanceof InvalidConfigurationException) {
                return CompletableFuture.completedFuture(
                    Results.internalServerError(getErrorResponse(cause, true))
                );
            }
            if (cause instanceof ExternalDependencyException) {
                return CompletableFuture.completedFuture(
                    Results.internalServerError(getErrorResponse(cause, true))
                );
            }
            if (cause instanceof InvalidInputException) {
                return CompletableFuture.completedFuture(
                    Results.badRequest(getErrorResponse(cause, true))
                );
            }
            if (cause instanceof ResourceOutOfDateException) {
                return CompletableFuture.completedFuture(
                    Results.status(Status.CONFLICT, getErrorResponse(cause, true))
                );
            } else {
                return CompletableFuture.completedFuture(
                    Results.internalServerError(getErrorResponse(cause, true))
                );
            }
        } else {
            return CompletableFuture.completedFuture(
                Results.internalServerError(getErrorResponse(e, true))
            );
        }
    }

    private JsonNode getErrorResponse(Throwable e, boolean stackTrace) {
        HashMap<String, Object> errorResult = new HashMap<String, Object>() {
            {
                put("Message", "An error has occured");
                put("ExceptionMessage", e.getMessage());
                put("ExceptionType", e.getClass().getName());
            }
        };

        if (stackTrace) {
            errorResult.put("StackTrace", e.getStackTrace());
            Throwable innerException = e.getCause();
            if (innerException != null) {
                errorResult.put("InnerExceptionMessage", innerException.getMessage());
                errorResult.put("InnerExceptionType", innerException.getClass().getName());
                errorResult.put("InnerExceptionStackTrace", innerException.getStackTrace());
            }
        }

        return toJson(errorResult);
    }
}
