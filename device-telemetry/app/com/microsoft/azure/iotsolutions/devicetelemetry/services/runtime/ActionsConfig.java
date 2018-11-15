// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime;

public class ActionsConfig {

    private final String eventHubName;
    private final String eventHubConnectionString;
    private final int eventHubOffsetTimeInMinutes;
    private final String blobStorageConnectionString;
    private final String eventHubCheckpointContainerName;
    private final String logicAppEndpointUrl;
    private final String solutionWebsiteUrl;
    private final String templateFolder;
    
    public ActionsConfig(
        String eventHubName,
        String eventHubConnectionString,
        int eventHubOffsetTimeInMinutes,
        String blobStorageConnectionString,
        String eventHubCheckpointContainerName,
        String logicAppEndpointUrl,
        String solutionWebsiteUrl,
        String templateFolder) {
        this.eventHubName = eventHubName;
        this.eventHubConnectionString = eventHubConnectionString;
        this.eventHubOffsetTimeInMinutes = eventHubOffsetTimeInMinutes;
        this.blobStorageConnectionString = blobStorageConnectionString;
        this.eventHubCheckpointContainerName = eventHubCheckpointContainerName;
        this.logicAppEndpointUrl = logicAppEndpointUrl;
        this.solutionWebsiteUrl = solutionWebsiteUrl;
        this.templateFolder = templateFolder;
    }

    public String getEventHubName() {
        return this.eventHubName;
    }

    public String getEventHubConnectionString() {
        return this.eventHubConnectionString;
    }

    public int getEventHubOffsetTimeInMinutes() {
        return this.eventHubOffsetTimeInMinutes;
    }

    public String getLogicAppEndpointUrl() {
        return this.logicAppEndpointUrl;
    }

    public String getSolutionWebsiteUrl() {
        return this.solutionWebsiteUrl;
    }

    public String getTemplateFolder() {
        return this.templateFolder;
    }

    public String getBlobStorageConnectionString() {
        return this.blobStorageConnectionString;
    }

    public String getEventHubCheckpointContainerName() {
        return this.eventHubCheckpointContainerName;
    }
}
