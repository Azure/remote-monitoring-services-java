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
        return eventHubName;
    }

    public String getEventHubConnectionString() {
        return eventHubConnectionString;
    }

    public int getEventHubOffsetTimeInMinutes() {
        return eventHubOffsetTimeInMinutes;
    }

    public String getLogicAppEndpointUrl() {
        return logicAppEndpointUrl;
    }

    public String getSolutionWebsiteUrl() {
        return solutionWebsiteUrl;
    }

    public String getTemplateFolder() {
        return templateFolder;
    }

    public String getBlobStorageConnectionString() {
        return blobStorageConnectionString;
    }

    public String getEventHubCheckpointContainerName() {
        return eventHubCheckpointContainerName;
    }
}
