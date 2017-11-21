// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.external;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceTwinServiceModel;

import java.util.concurrent.CompletionStage;

@ImplementedBy(ConfigService.class)
public interface IConfigService {

    CompletionStage updateDeviceGroupFiltersAsync(DeviceTwinServiceModel twin);

}
