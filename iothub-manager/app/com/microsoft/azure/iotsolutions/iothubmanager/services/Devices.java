// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceServiceModel;
import com.microsoft.azure.sdk.iot.service.Device;
import com.microsoft.azure.sdk.iot.service.RegistryManager;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

// TODO: handle exceptions
// TODO: logging
// TODO: documentation

public final class Devices implements IDevices {

    private static final int MAX_GET_LIST = 1000;

    private final RegistryManager registry;
    private final IDeviceTwins deviceTwins;
    IIoTHubWrapper _ioTHubService;

    @Inject
    public Devices(
        final IIoTHubWrapper ioTHubService,
        final IDeviceTwins deviceTwins)
        throws Exception {
        _ioTHubService = ioTHubService;
        this.registry = ioTHubService.getRegistryManagerClient();
        this.deviceTwins = deviceTwins;
    }

    public CompletableFuture<ArrayList<DeviceServiceModel>> getListAsync() {
        try {

            return this.registry.getDevicesAsync(MAX_GET_LIST)
                .thenApply(devices -> {
                    ArrayList<DeviceServiceModel> result = new ArrayList<>();
                    for (Device device : devices) {
                        result.add(new DeviceServiceModel(device, null));
                    }
                    return result;
                });
        } catch (IOException e) {
            // TODO
            return null;
        } catch (IotHubException e) {
            // TODO
            return null;
        }
    }

    public CompletableFuture<DeviceServiceModel> getAsync(final String id) {
        try {
            return this.registry.getDeviceAsync(id)
                .thenApply(device -> new DeviceServiceModel(device, this.deviceTwins.get(id)));
        } catch (IOException e) {
            // TODO
            return null;
        } catch (IotHubException e) {
            // TODO
            return null;
        }
    }

    public CompletableFuture<DeviceServiceModel> createAsync(final DeviceServiceModel device) {
        // TODO
        return null;
    }
}
