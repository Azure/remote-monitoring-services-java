// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime;

/**
 * Service layer configuration
 */
public class ServicesConfig implements IServicesConfig {

    private final String keyValueStorageUrl;

    private final String userManagementApiUrl;

    private final MessagesConfig messagesConfig;

    private final AlarmsConfig alarmsConfig;

    private final ActionsConfig actionsConfig;

    private final DiagnosticsConfig diagnosticsConfig;

    public ServicesConfig(
        final String keyValueStorageUrl,
        final String userManagementApiUrl,
        MessagesConfig messagesConfig,
        AlarmsConfig alarmsConfig,
        ActionsConfig actionsConfig,
        DiagnosticsConfig diagnosticsConfig) {
        this.keyValueStorageUrl = keyValueStorageUrl;
        this.userManagementApiUrl = userManagementApiUrl;
        this.messagesConfig = messagesConfig;
        this.alarmsConfig = alarmsConfig;
        this.actionsConfig = actionsConfig;
        this.diagnosticsConfig = diagnosticsConfig;
    }

    /**
     * Get key value storage dependency url
     *
     * @return url for key value storage endpoint
     */
    public String getKeyValueStorageUrl() {
        return this.keyValueStorageUrl;
    }

    /**
     * Get user management dependency url
     *
     * @return url for user management endpoint
     */
    public String getUserManagementApiUrl() {
        return this.userManagementApiUrl;
    }

    public MessagesConfig getMessagesConfig() {
        return this.messagesConfig;
    }

    public AlarmsConfig getAlarmsConfig() {
        return this.alarmsConfig;
    }

    public ActionsConfig getActionsConfig() {
        return this.actionsConfig;
    }

    public DiagnosticsConfig getDiagnosticsConfig() {
        return this.diagnosticsConfig;
    }
}
