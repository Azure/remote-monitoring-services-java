// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.auth;

/**
 * This exception is thrown when something goes wrong when talking
 * with an external dependency.
 */
public class ExternalDependencyException extends Exception {

    public ExternalDependencyException() {
        super();
    }

    public ExternalDependencyException(String message) {
        super(message);
    }

    public ExternalDependencyException(String message, Exception innerException) {
        super(message, innerException);
    }
}
