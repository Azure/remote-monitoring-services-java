// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.helpers;

import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.NotAuthorizedException;
import org.apache.http.HttpStatus;
import play.Logger;
import play.libs.ws.WSResponse;
import play.mvc.Http;

import java.util.concurrent.CompletionException;

public class WsResponseHelper {
    private static final Logger.ALogger log = Logger.of(WsResponseHelper.class);

    /***
     * Helper method that throws if the status code returned by a service is
     * an unauthorized exception (401 or 403).
     *
     * @param response
     * @throws CompletionException
     */
    public static void checkUnauthorizedStatus(WSResponse response) throws CompletionException {
        if (response != null) {
            // If the error is 403 or 401, the user who did the deployment is not authorized
            // to assign the role for the application to have Contributor access.
            if (response.getStatus() == HttpStatus.SC_FORBIDDEN ||
                    response.getStatus() == HttpStatus.SC_UNAUTHORIZED) {
                String message = String.format("The application is not authorized and has not been " +
                        "assigned Contributor permissions for the subscription. Go to the Azure portal and " +
                        "assign the application as a Contributor in order to retrieve the token.");
                log.error(message);
                throw new CompletionException(new NotAuthorizedException(message));
            }
        }
    }

    /****
     * Helper method that checks if there was an error returned from an external service.
     *
     * @param error error returned with response if any
     * @param message message to log if error or non-success
     * @throws CompletionException
     */
    public static void checkError(Throwable error, String message) throws CompletionException {
        if (error != null) {
            log.error(message, error.getCause());
            throw new CompletionException(new ExternalDependencyException(message, error.getCause()));
        }
    }

    /****
     * Helper method that checks if the response from an external service is a success.
     *
     * @param message message to log if non-success status code
     * @throws CompletionException
     */
    public static void checkSuccessStatusCode(WSResponse response, String message) {
        if (response != null) {
            if (response.getStatus() != Http.Status.OK) {
                log.error(message);
                throw new CompletionException(new ExternalDependencyException(message));
            }
        }
    }
}
