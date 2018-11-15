// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.runtime;

public interface IActionsConfig {

    String getOffice365LogicAppUrl();

    String getResourceGroup();

    String getSubscriptionId();

    String getManagementApiVersion();

    String getArmEndpointUrl();
}
