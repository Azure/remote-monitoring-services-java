// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.runtime;

/**
 * Service configuration settings for Actions
 */
public class ActionsConfig implements IActionsConfig {

    private String armEndpointUrl;
    private String managementApiVersion;
    private String office365LogicAppUrl;
    private String resourceGroup;
    private String subscriptionId;

    public ActionsConfig(String armEndpointUrl,
                         String managementApiVersion,
                         String office365LogicAppUrl,
                         String resourceGroup,
                         String subscriptionId) {
        this.armEndpointUrl = armEndpointUrl;
        this.managementApiVersion = managementApiVersion;
        this.office365LogicAppUrl = office365LogicAppUrl;
        this.resourceGroup = resourceGroup;
        this.subscriptionId = subscriptionId;
    }

    @Override
    public String getArmEndpointUrl() {
        return this.armEndpointUrl;
    }

    @Override
    public String getManagementApiVersion() {
        return this.managementApiVersion;
    }

    @Override
    public String getOffice365LogicAppUrl() {
        return this.office365LogicAppUrl;
    }

    @Override
    public String getResourceGroup() {
        return this.resourceGroup;
    }

    @Override
    public String getSubscriptionId() {
        return this.subscriptionId;
    }
}
