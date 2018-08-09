// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.exceptions;

/**
 * Checked exception for meaningful errors to client side.
 *
 * This base exception is thrown when a client is requesting a resource.
 * Define new exceptions as subclass of this base exception will make the
 * exception handling more graceful.
 */
public class BaseException extends Exception {
    public BaseException() {
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
