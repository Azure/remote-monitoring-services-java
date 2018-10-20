// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime;

import com.google.inject.ImplementedBy;

@ImplementedBy(ServicesConfig.class)
public interface IServiceConfig {
    /**
     * Get key value storage dependency url
     */
    String getKeyValueStorageUrl();

    MessagesConfig getMessagesConfig();

    AlarmsConfig getAlarmsConfig();

    ActionsConfig getActionsConfig();

    DiagnosticsConfig getDiagnosticsConfig();
}
