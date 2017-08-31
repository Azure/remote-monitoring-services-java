// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.*;
import com.microsoft.azure.iotsolutions.iothubmanager.services.helpers.QueryConditionTranslator;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceServiceModel;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceServiceListModel;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceTwinServiceModel;
import com.microsoft.azure.sdk.iot.service.*;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceTwin;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceTwinDevice;
import com.microsoft.azure.sdk.iot.service.devicetwin.Query;
import com.microsoft.azure.sdk.iot.service.exceptions.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

import play.Logger;

public final class Devices implements IDevices {

    private static final Logger.ALogger log = Logger.of(Devices.class);

    private static final int MAX_GET_LIST = 1000;
    private static final String QueryPrefix = "SELECT * FROM devices";

    private final RegistryManager registry;
    private final DeviceTwin deviceTwinClient;
    private final String iotHubHostName;
    IIoTHubWrapper _ioTHubService;

    @Inject
    public Devices(final IIoTHubWrapper ioTHubService) throws Exception {
        _ioTHubService = ioTHubService;
        this.registry = ioTHubService.getRegistryManagerClient();
        this.deviceTwinClient = ioTHubService.getDeviceTwinClient();
        this.iotHubHostName = ioTHubService.getIotHubHostName();
    }

    public CompletionStage<DeviceServiceModel> getAsync(final String id) throws IOException, IotHubException {
        return this.registry.getDeviceAsync(id)
            .handle((device, error) -> {
                if (error != null) {
                    if (error instanceof IotHubNotFoundException) {
                        throw new CompletionException(
                            new ResourceNotFoundException("Unable to get device: " + id, error));
                    } else {
                        throw new CompletionException(error);
                    }
                }

                try {
                    DeviceTwinDevice twin = new DeviceTwinDevice(id);
                    this.deviceTwinClient.getTwin(twin);
                    return new DeviceServiceModel(device, new DeviceTwinServiceModel(twin), this.iotHubHostName);
                } catch (IOException | IotHubException e) {
                    throw new CompletionException(
                        new ExternalDependencyException("Unable to retrieve twin of device: " + id, e));
                }
            });
    }

    public CompletionStage<DeviceServiceListModel> queryAsync(final String query, String continuationToken) throws
        IOException,
        IotHubException,
        InvalidInputException,
        ExternalDependencyException {
        // normally we need deviceTwins for all devices to show device list
        return this.registry.getDevicesAsync(MAX_GET_LIST)
            .handle((devices, error) -> {
                if (error != null) {
                    throw new CompletionException(new ExternalDependencyException("Unable to get devices", error));
                }

                try {
                    HashMap<String, DeviceTwinServiceModel> twins = GetTwinByQueryAsync(QueryConditionTranslator.ToQueryString(query), continuationToken, MAX_GET_LIST);
                    ArrayList<DeviceServiceModel> deviceList = new ArrayList<>();
                    for (Device azureDevice : devices) {
                        if (twins.containsKey(azureDevice.getDeviceId())) {
                            deviceList.add(new DeviceServiceModel(
                                azureDevice,
                                twins.get(azureDevice.getDeviceId()),
                                this.iotHubHostName));
                        }
                    }
                    return new DeviceServiceListModel(deviceList, continuationToken);
                } catch (InvalidInputException | ExternalDependencyException e) {
                    throw new CompletionException("Unable to retrieve twin of devices", e);
                }
            });
    }

    public CompletionStage<DeviceServiceModel> createAsync(final DeviceServiceModel device) throws InvalidInputException, IOException, IotHubException {
        return this.registry.addDeviceAsync(device.toAzureModel())
            .handle((azureDevice, error) -> {
                if (error != null) {
                    throw new CompletionException(error);
                }

                try {
                    DeviceTwinDevice twin = new DeviceTwinDevice(device.getId());
                    if (device.getTwin() == null) {
                        this.deviceTwinClient.getTwin(twin);
                        return new DeviceServiceModel(azureDevice, new DeviceTwinServiceModel(twin), this.iotHubHostName);
                    } else {
                        this.deviceTwinClient.updateTwin(device.getTwin().toDeviceTwinDevice());
                        return new DeviceServiceModel(azureDevice, device.getTwin(), this.iotHubHostName);
                    }
                } catch (IOException | IotHubException e) {
                    throw new CompletionException(
                        new ExternalDependencyException("Unable to create new device", e));
                }
            });
    }

    public CompletionStage<DeviceServiceModel> createOrUpdateAsync(final String id, final DeviceServiceModel device) throws BaseException, IOException, IotHubException {
        if (device.getId() == null || device.getId().isEmpty()) {
            throw new InvalidInputException("Device id is empty");
        }

        if (!device.getId().equals(id)) {
            throw new InvalidInputException("Mismatched device id in the request");
        }

        return this.registry.getDeviceAsync(id)
            .handle((azureDevice, error) -> {
                if (error != null || azureDevice == null) {
                    try {
                        azureDevice = this.registry.addDeviceAsync(device.toAzureModel()).get();
                    } catch (Exception e) {
                        throw new CompletionException("Unable to create new device", e);
                    }
                }

                try {
                    DeviceTwinDevice twin = new DeviceTwinDevice(device.getId());
                    if (device.getTwin() == null) {
                        this.deviceTwinClient.getTwin(twin);
                        return new DeviceServiceModel(azureDevice, new DeviceTwinServiceModel(twin), this.iotHubHostName);
                    } else {
                        this.deviceTwinClient.updateTwin(device.getTwin().toDeviceTwinDevice());
                        return new DeviceServiceModel(azureDevice, device.getTwin(), this.iotHubHostName);
                    }
                } catch (IOException | IotHubException e) {
                    throw new CompletionException(
                        new ExternalDependencyException("Unable to create new device", e));
                }
            });
    }

    public CompletionStage<Boolean> deleteAsync(final String id) throws IOException, IotHubException {
        return this.registry.removeDeviceAsync(id)
            .exceptionally(error -> {
                if (error instanceof IotHubNotFoundException) {
                    throw new CompletionException(new ResourceNotFoundException("Unable to delete non-exist device: " + id, error));
                } else {
                    throw new CompletionException(new ExternalDependencyException("Unable to delete device" + id, error));
                }
            });
    }

    private HashMap<String, DeviceTwinServiceModel> GetTwinByQueryAsync(final String query, String continuationToken, int nubmerOfResult) throws ExternalDependencyException {
        String fullQuery = query.isEmpty() ? QueryPrefix : String.format("%s where %s", QueryPrefix, query);

        HashMap<String, DeviceTwinServiceModel> twins = new HashMap();

        try {
            Query twinQuery = this.deviceTwinClient.queryTwin(fullQuery);
            while (this.deviceTwinClient.hasNextDeviceTwin(twinQuery)) {
                DeviceTwinDevice twin = this.deviceTwinClient.getNextDeviceTwin(twinQuery);
                twins.put(twin.getDeviceId(), new DeviceTwinServiceModel(twin));
            }
        } catch (IotHubException | IOException e) {
            throw new ExternalDependencyException("Unable to query device twin", e);
        }

        return twins;
    }
}
