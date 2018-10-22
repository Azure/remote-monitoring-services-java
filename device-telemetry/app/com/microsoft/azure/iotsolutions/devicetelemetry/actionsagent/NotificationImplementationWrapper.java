// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.actions.IActionExecutor;
import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.actions.EmailActionExecutor;
import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.INotification.EmailImplementationTypes;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServiceConfig;
import play.libs.ws.WSClient;

public class NotificationImplementationWrapper implements INotificationImplementationWrapper {
    private IServiceConfig servicesConfig;
    private WSClient client;

    @Inject
    public NotificationImplementationWrapper(WSClient client, IServiceConfig servicesConfig) {
        this.client = client;
        this.servicesConfig = servicesConfig;
    }

    @Override
    public IActionExecutor getImplementationType(EmailImplementationTypes actionType) {
        switch (actionType) {
            case LogicApp:
                return new EmailActionExecutor(
                    this.servicesConfig.getActionsConfig().getLogicAppEndpointUrl(),
                    this.servicesConfig.getActionsConfig().getSolutionWebsiteUrl(),
                    this.client);
            default:
                throw new IllegalArgumentException("Improper action type");
        }
    }
}
