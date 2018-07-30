// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.webservice.v1.exceptions;

/**
 * Checked exception for request errors.
 *
 * This exception is thrown by a controller when the input validation
 * fails. The client should fix the request before retrying.
 */
public class BadRequestException extends Exception {
    public BadRequestException() {
    }

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
