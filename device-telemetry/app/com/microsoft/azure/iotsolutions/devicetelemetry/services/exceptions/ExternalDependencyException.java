// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions;

/**
 * Checked exception for request errors.
 * This exception is thrown when a client attempts to create a resource
 * which would conflict with an existing one, for instance using the same
 * identifier. The client should change the identifier or assume the
 * resource has already been created.
 */
public class ExternalDependencyException extends Exception {
    public ExternalDependencyException() {
    }

    public ExternalDependencyException(String message) {
        super(message);
    }

    public ExternalDependencyException(String message, Throwable cause) {
        super(message, cause);
    }
}
