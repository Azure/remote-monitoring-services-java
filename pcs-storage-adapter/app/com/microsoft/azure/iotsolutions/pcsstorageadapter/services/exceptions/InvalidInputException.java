// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.services.exceptions;

/**
 * Checked exception for request errors.
 *
 * This exception is thrown when a client sends a request badly formatted
 * or containing invalid values. The client should fix the request before
 * retrying.
 */
public class InvalidInputException extends Exception {
    public InvalidInputException() {
    }

    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }
}
