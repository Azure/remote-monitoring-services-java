// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.exceptions;

/**
 * Checked exception for request errors.
 *
 * This exception is thrown when a client is requesting
 * a resource and they do not have the correct permissions.
 */
public class ResourceNotFoundException extends BaseException {
    public ResourceNotFoundException() {
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
