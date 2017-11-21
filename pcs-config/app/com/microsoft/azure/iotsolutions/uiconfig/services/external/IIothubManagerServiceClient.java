// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.DeviceTwinName;

import java.net.URISyntaxException;
import java.util.concurrent.CompletionStage;

@ImplementedBy(IothubManagerServiceClient.class)
public interface IIothubManagerServiceClient {
    CompletionStage<DeviceTwinName> getDeviceTwinNamesAsync() throws URISyntaxException;
}
