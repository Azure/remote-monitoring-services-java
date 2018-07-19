package com.microsoft.azure.iotsolutions.devicetelemetry.services.notification;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.INotification.EmailImplementationTypes;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.implementation.IImplementation;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.implementation.LogicApp;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServicesConfig;
import play.Logger;
import play.libs.ws.WSClient;

public class ImplementationWrapper implements IImplementationWrapper{
    private IServicesConfig servicesConfig;
    private WSClient wsClient;

    @Inject
    public ImplementationWrapper(IServicesConfig servicesConfig, WSClient wsClient) {
        this.servicesConfig = servicesConfig;
        this.wsClient = wsClient;
    }

    @Override
    public IImplementation getImplementationType(EmailImplementationTypes actionType) {
        switch (actionType) {
            case LogicApp:
                return new LogicApp(this.servicesConfig.getLogicAppEndPointUrl(), this.servicesConfig.getSolutionName(), this.wsClient);
            default:
                throw new IllegalArgumentException("Improper action type");
        }
    }
}
