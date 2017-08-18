// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.services.exceptions;

/**
 * Checked exception for request errors.
 * <p>
 * This exception is thrown when a client attempts to create a resource
 */
public class CreateResourceException extends Exception {
    public CreateResourceException() {
    }

    public CreateResourceException(String message) {
        super(message);
    }

    public CreateResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
