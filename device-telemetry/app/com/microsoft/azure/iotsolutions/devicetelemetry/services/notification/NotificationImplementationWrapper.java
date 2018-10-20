// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.notification;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.INotification.EmailImplementationTypes;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.implementation.INotificationImplementation;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.implementation.LogicApp;
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
    public INotificationImplementation getImplementationType(EmailImplementationTypes actionType) {
        switch (actionType) {
            case LogicApp:
                return new LogicApp(
                    this.servicesConfig.getActionsConfig().getLogicAppEndpointUrl(),
                    this.servicesConfig.getActionsConfig().getSolutionWebsiteUrl(),
                    this.client);
            default:
                throw new IllegalArgumentException("Improper action type");
        }
    }
}
