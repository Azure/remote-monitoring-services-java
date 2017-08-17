// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.services.exceptions;

/**
 * Checked exception for internal errors.
 *
 * This exception is thrown when the service is configured incorrectly.
 * In order to recover, the service owner should fix the configuration
 * and re-deploy the service.
 */
public class InvalidConfigurationException extends Exception {
    public InvalidConfigurationException() {
    }

    public InvalidConfigurationException(String message) {
        super(message);
    }

    public InvalidConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
