// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.exceptions;

/**
 * This exception is thrown by a controller when the input validation
 * fails. The client should fix the request before retrying.
 */
public class BadRequestException extends Exception {

    public BadRequestException () {
        super();
    }

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Exception innerException){
        super(message, innerException);
    }
}
