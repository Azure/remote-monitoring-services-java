// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.exceptions;

/**
 * Checked exception for request errors.
 *
 * This exception is thrown when a client sends a request badly formatted
 * or containing invalid values. The client should fix the request before
 * retrying.
 */
public class NotAuthorizedException extends BaseException {
    public NotAuthorizedException() {
    }

    public NotAuthorizedException(String message) {
        super(message);
    }

    public NotAuthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
