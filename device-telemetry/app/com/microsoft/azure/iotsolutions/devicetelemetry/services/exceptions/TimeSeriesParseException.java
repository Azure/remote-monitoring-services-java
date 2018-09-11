// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions;

/**
 * Checked exception for request errors.
 * This exception is thrown when parsing response from Time Series Insights.
 */
public class TimeSeriesParseException extends Exception {
    public TimeSeriesParseException() {
    }

    public TimeSeriesParseException(String message) {
        super(message);
    }

    public TimeSeriesParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
