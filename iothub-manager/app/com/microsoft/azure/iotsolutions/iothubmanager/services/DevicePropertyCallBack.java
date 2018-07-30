package com.microsoft.azure.iotsolutions.iothubmanager.services;

import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.BaseException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DevicePropertyServiceModel;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

public interface DevicePropertyCallBack {
    public CompletionStage updateCache(DevicePropertyServiceModel devices) throws InterruptedException, ExecutionException, BaseException, BaseException;
}
