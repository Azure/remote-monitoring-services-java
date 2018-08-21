// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime;

public class DiagnosticsConfig {
    private final String apiUrl;
    private final int maxLogRetries;

    public DiagnosticsConfig(String apiUrl, int maxLogRetries)
    {
        this.apiUrl = apiUrl;
        this.maxLogRetries = maxLogRetries;
    }

    public String getApiUrl() { return this.apiUrl; }

    public int getMaxLogRetries() { return this.maxLogRetries; }
}
