// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime;

/**
 * Service layer configuration
 */
public class ServicesConfig implements IServicesConfig {

    private final String keyValueStorageUrl;

    private final MessagesConfig messagesConfig;

    private final AlarmsConfig alarmsConfig;

    private final DiagnosticsConfig diagnosticsConfig;

    private final String eventHubName;

    private final String eventHubConnectionString;

    private final int eventHubOffsetTimeInMinutes;

    private final String logicAppEndPointUrl;

    private final String solutionName;

    public ServicesConfig(
            final String keyValueStorageUrl,
            MessagesConfig messagesConfig,
            AlarmsConfig alarmsConfig,
            DiagnosticsConfig diagnosticsConfig,
            final String eventHubName,
            final String eventHubConnectionString,
            final int eventHubOffsetTimeInMinutes,
            final String logicAppEndPointUrl,
            final String solutionName) {
        this.keyValueStorageUrl = keyValueStorageUrl;
        this.messagesConfig = messagesConfig;
        this.alarmsConfig = alarmsConfig;
        this.diagnosticsConfig = diagnosticsConfig;
        this.eventHubName = eventHubName;
        this.eventHubConnectionString = eventHubConnectionString;
        this.eventHubOffsetTimeInMinutes = eventHubOffsetTimeInMinutes;
        this.logicAppEndPointUrl = logicAppEndPointUrl;
        this.solutionName = solutionName;
    }

    /**
     * Get key value storage dependency url
     *
     * @return url for key value storage endpoint
     */
    public String getKeyValueStorageUrl() {
        return this.keyValueStorageUrl;
    }

    public MessagesConfig getMessagesConfig() {
        return this.messagesConfig;
    }

    public AlarmsConfig getAlarmsConfig() {
        return this.alarmsConfig;
    }

    public DiagnosticsConfig getDiagnosticsConfig() {
        return this.diagnosticsConfig;
    }


    @Override
    public String getEventHubName() {
        return this.eventHubName;
    }

    @Override
    public String getEventHubConnectionString() { return this.eventHubConnectionString; }

    @Override
    public int getEventHubOffsetTimeInMinutes() {
        return this.eventHubOffsetTimeInMinutes;
    }

    @Override
    public String getLogicAppEndPointUrl() {
        return this.logicAppEndPointUrl;
    }

    @Override
    public String getSolutionName() { return this.solutionName; }
}
