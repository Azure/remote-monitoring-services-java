// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceTwinServiceModel;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceTwin;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceTwinDevice;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;

import java.io.IOException;
import java.util.ArrayList;

// TODO: handle exceptions
// TODO: logging
// TODO: support pagination

/**
 * Azure IoT Device Twins management.
 */
public final class DeviceTwins implements IDeviceTwins {

    private static final int PAGE_SIZE = 1000;

    private final DeviceTwin registry;

    /**
     * Create a new instance.
     *
     * @throws Exception
     */
    @Inject
    public DeviceTwins(final IIoTHubWrapper ioTHubService) throws Exception {
        this.registry = ioTHubService.getDeviceTwinClient();
    }

    /**
     * Get one device twin.
     *
     * @param id device id
     *
     * @return device twin
     *
     * @throws IOException
     * @throws IotHubException
     */
    public DeviceTwinServiceModel get(final String id) {
        try {
            DeviceTwinDevice device = new DeviceTwinDevice(id);
            this.registry.getTwin(device);
            return new DeviceTwinServiceModel(device);
        } catch (IotHubException e) {
            // TODO
            return null;
        } catch (IOException e) {
            // TODO
            return null;
        }
    }

    /**
     * Get a list of device twin, with pagination.
     *
     * @return page of device twins
     */
    public ArrayList<DeviceTwinServiceModel> getList() {
        // TODO
        return null;
    }
}
