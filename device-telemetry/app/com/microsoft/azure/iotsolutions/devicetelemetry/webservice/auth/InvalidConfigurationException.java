// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.auth;

/**
 * This exception is thrown when something is wrong in the
 * service configuration.
 */
public class InvalidConfigurationException extends Exception {

    public InvalidConfigurationException() {
        super();
    }

    public InvalidConfigurationException(String message) {
        super(message);
    }

    public InvalidConfigurationException(String message, Exception innerException) {
        super(message, innerException);
    }
}
