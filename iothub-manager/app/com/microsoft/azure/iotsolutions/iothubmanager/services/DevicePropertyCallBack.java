package com.microsoft.azure.iotsolutions.iothubmanager.services;

import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DevicePropertyServiceModel;

import java.util.concurrent.CompletionStage;

public interface DevicePropertyCallBack {
    public CompletionStage updateCache(DevicePropertyServiceModel devices)
        throws InterruptedException, ExternalDependencyException;
}
