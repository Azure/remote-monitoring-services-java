// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.helpers;

import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.ConflictingResourceException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.ResourceNotFoundException;
import play.Logger;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;

public class HttpRequestHelper {
    private static final Logger.ALogger log = Logger.of(HttpRequestHelper.class);

    public static void checkStatusCode(WSResponse response, WSRequest request)
            throws ResourceNotFoundException, ConflictingResourceException, ExternalDependencyException {
        if (response.getStatus() == 200) {
            return;
        }

        log.info(String.format("Config returns %s for request %s",
                response.getStatus(), request.getUrl().toString()));

        switch (response.getStatus()) {
            case 404:
                throw new ResourceNotFoundException(String.format("%s, request URL = %s,",
                        response.getBody(), request.getUrl()));
            case 409:
                throw new ConflictingResourceException(String.format("%s, request URL = %s,",
                        response.getBody(), request.getUrl()));

            default:
                throw new ExternalDependencyException(
                        String.format("WS-request failed, status code = %s, content = %s, request URL = %s",
                                response.getStatus(), response.getBody(), request.getUrl()));
        }
    }
}
