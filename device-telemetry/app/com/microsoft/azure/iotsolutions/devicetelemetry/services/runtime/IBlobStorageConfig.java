// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime;

public interface IBlobStorageConfig {

    public String getAccountName();

    public String getAccountKey();

    public String getEndpointSuffix();

    public String getEventHubContainer();
}
