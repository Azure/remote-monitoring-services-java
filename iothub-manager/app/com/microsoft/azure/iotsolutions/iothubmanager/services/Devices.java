// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.*;
import com.microsoft.azure.iotsolutions.iothubmanager.services.external.IConfigService;
import com.microsoft.azure.iotsolutions.iothubmanager.services.helpers.QueryConditionTranslator;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.*;
import com.microsoft.azure.sdk.iot.service.*;
import com.microsoft.azure.sdk.iot.service.devicetwin.*;
import com.microsoft.azure.sdk.iot.service.exceptions.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

import play.Logger;
import play.libs.Json;

public final class Devices implements IDevices {

    private static final Logger.ALogger log = Logger.of(Devices.class);

    private static final int MAX_GET_LIST = 1000;
    private static final String QueryPrefix = "SELECT * FROM devices";

    private final RegistryManager registry;
    private final DeviceTwin deviceTwinClient;
    private final DeviceMethod deviceMethodClient;
    private final String iotHubHostName;
    IIoTHubWrapper _ioTHubService;
    private final IConfigService configService;

    @Inject
    public Devices(final IIoTHubWrapper ioTHubService, final IConfigService configService) throws Exception {
        _ioTHubService = ioTHubService;
        this.configService = configService;
        this.registry = ioTHubService.getRegistryManagerClient();
        this.deviceTwinClient = ioTHubService.getDeviceTwinClient();
        this.deviceMethodClient = ioTHubService.getDeviceMethodClient();
        this.iotHubHostName = ioTHubService.getIotHubHostName();
    }

    public CompletionStage<DeviceServiceModel> getAsync(final String id) throws ExternalDependencyException {
        try {
            return this.registry.getDeviceAsync(id)
                .handle((device, error) -> {
                    if (error != null) {
                        String message = String.format("Unable to get device by id: %s", id);
                        log.error(message, error);
                        if (error instanceof IotHubNotFoundException) {
                            throw new CompletionException(
                                new ResourceNotFoundException(message, error));
                        } else {
                            throw new CompletionException(message, error);
                        }
                    }

                    try {
                        DeviceTwinDevice twin = new DeviceTwinDevice(id);
                        this.deviceTwinClient.getTwin(twin);
                        return new DeviceServiceModel(device, new DeviceTwinServiceModel(twin), this.iotHubHostName);
                    } catch (IOException | IotHubException e) {
                        String message = String.format("Unable to retrieve device twin by id: %s", id);
                        log.error(message, error);
                        throw new CompletionException(
                            new ExternalDependencyException(message, e));
                    }
                });
        } catch (IOException | IotHubException e) {
            String message = String.format("Unable to get device by id: %s", id);
            log.error(message, e);
            throw new ExternalDependencyException(message, e);
        }
    }

    public CompletionStage<DeviceServiceListModel> queryAsync(final String query, String continuationToken) throws
        ExternalDependencyException {
        try {
            // normally we need deviceTwins for all devices to show device list
            return this.registry.getDevicesAsync(MAX_GET_LIST)
                .handle((devices, error) -> {
                    if (error != null) {
                        String message = String.format("Unable to get device by query: %s", query);
                        log.error(message, error);
                        throw new CompletionException(new ExternalDependencyException(message, error));
                    }

                    try {
                        HashMap<String, DeviceTwinServiceModel> twins = GetTwinByQueryAsync(
                            QueryConditionTranslator.ToQueryString(query),
                            continuationToken,
                            MAX_GET_LIST);
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
                        String message = String.format("Unable to get device twin by query: %s", query);
                        log.error(message, error);
                        throw new CompletionException(message, e);
                    }
                });
        } catch (IOException | IotHubException e) {
            String message = String.format("Unable to get devices with max count: %d", MAX_GET_LIST);
            log.error(message, e);
            throw new ExternalDependencyException(message, e);
        }
    }

    public CompletionStage<DeviceServiceModel> createAsync(
        final DeviceServiceModel device)
        throws InvalidInputException, ExternalDependencyException {
        if (device.getId() == null || device.getId().isEmpty()) {
            device.setId(UUID.randomUUID().toString());
        }

        try {
            return this.registry.addDeviceAsync(device.toAzureModel())
                .handle((azureDevice, error) -> {
                    if (error != null) {
                        String message = String.format("Unable to create new device: %s", device.getId());
                        log.error(message, error);
                        throw new CompletionException(message, error);
                    }

                    try {
                        DeviceTwinServiceModel twinServiceModel = device.getTwin();
                        DeviceTwinDevice azureTwin = new DeviceTwinDevice(device.getId());
                        if (twinServiceModel == null) {
                            this.deviceTwinClient.getTwin(azureTwin);
                            return new DeviceServiceModel(azureDevice, new DeviceTwinServiceModel(azureTwin), this.iotHubHostName);
                        } else {
                            if (twinServiceModel.getDeviceId() == null || twinServiceModel.getDeviceId().isEmpty()) {
                                twinServiceModel.setDeviceId(device.getId());
                            }
                            if (twinServiceModel.getProperties() != null || twinServiceModel.getTags() != null) {
                                this.deviceTwinClient.updateTwin(twinServiceModel.toDeviceTwinDevice());
                            }
                            return new DeviceServiceModel(azureDevice, device.getTwin(), this.iotHubHostName);
                        }
                    } catch (IOException | IotHubException e) {
                        String message = String.format("Unable to get or update twin of device: %s", device.getId());
                        log.error(message, e);
                        throw new CompletionException(
                            new ExternalDependencyException(message, e));
                    }
                });
        } catch (IOException | IotHubException e) {
            String message = String.format("Unable to create new device: %s", device.getId());
            log.error(message, e);
            throw new ExternalDependencyException(message, e);
        }
    }

    public CompletionStage<DeviceServiceModel> createOrUpdateAsync(
        final String id, final DeviceServiceModel device)
        throws InvalidInputException, ExternalDependencyException {
        if (device.getId() == null || device.getId().isEmpty()) {
            throw new InvalidInputException("Device id is empty");
        }

        if (!device.getId().equals(id)) {
            throw new InvalidInputException("Mismatched device id in the request");
        }

        try {
            return this.registry.getDeviceAsync(id)
                .handle((azureDevice, error) -> {
                    if (error != null || azureDevice == null) {
                        try {
                            azureDevice = this.registry.addDeviceAsync(device.toAzureModel()).get();
                        } catch (Exception e) {
                            String message = String.format("Unable to create new device: %s", id);
                            log.error(message, e);
                            throw new CompletionException(message, e);
                        }
                    }

                    try {
                        DeviceTwinDevice twin = new DeviceTwinDevice(device.getId());
                        if (device.getTwin() == null) {
                            this.deviceTwinClient.getTwin(twin);
                            return new DeviceServiceModel(azureDevice, new DeviceTwinServiceModel(twin), this.iotHubHostName);
                        } else {
                            this.deviceTwinClient.updateTwin(device.getTwin().toDeviceTwinDevice());
                            // Update the deviceGroupFilter cache, no need to wait
                            this.configService.updateDeviceGroupFiltersAsync(device.getTwin());
                            return new DeviceServiceModel(azureDevice, device.getTwin(), this.iotHubHostName);
                        }
                    } catch (IOException | IotHubException e) {
                        String message = String.format("Unable to get or update twin of device: %s", id);
                        log.error(message, e);
                        throw new CompletionException(
                            new ExternalDependencyException(message, e));
                    }
                });
        } catch (IOException | IotHubException e) {
            String message = String.format("Unable to get device by id: %s", id);
            log.error(message, e);
            throw new ExternalDependencyException(message, e);
        }
    }

    public CompletionStage<Boolean> deleteAsync(final String id) throws ExternalDependencyException {
        try {
            return this.registry.removeDeviceAsync(id)
                .exceptionally(error -> {
                    if (error instanceof IotHubNotFoundException) {
                        throw new CompletionException(new ResourceNotFoundException("Unable to delete non-exist device: " + id, error));
                    } else {
                        throw new CompletionException(new ExternalDependencyException("Unable to delete device" + id, error));
                    }
                });
        } catch (IOException | IotHubException e) {
            String message = String.format("Unable to get device by id: %s", id);
            log.error(message, e);
            throw new ExternalDependencyException(message, e);
        }
    }

    public CompletionStage<MethodResultServiceModel> invokeDeviceMethodAsync(
        final String id,
        MethodParameterServiceModel parameter)
        throws ExternalDependencyException {
        try {
            MethodResult result = this.deviceMethodClient.invoke(
                id, parameter.getName(),
                parameter.getResponseTimeout().getSeconds(),
                parameter.getConnectionTimeout().getSeconds(),
                parameter.getJsonPayload());
            return CompletableFuture.supplyAsync(() -> new MethodResultServiceModel(result));
        } catch (IOException | IotHubException e) {
            String message = String.format("Unable to invoke device method: %s, %s",
                id, Json.stringify(Json.toJson(parameter)));
            log.error(message, e);
            throw new ExternalDependencyException(message, e);
        }
    }

    private HashMap<String, DeviceTwinServiceModel> GetTwinByQueryAsync(
        final String query, String continuationToken, int nubmerOfResult)
        throws ExternalDependencyException {
        String fullQuery = query.isEmpty() ? QueryPrefix : String.format("%s where %s", QueryPrefix, query);
        QueryOptions options = new QueryOptions();
        if (continuationToken != null && !continuationToken.isEmpty()) {
            options.setContinuationToken(continuationToken);
        }

        HashMap<String, DeviceTwinServiceModel> twins = new HashMap();
        try {
            QueryCollection twinQuery = this.deviceTwinClient.queryTwinCollection(fullQuery);
            while (this.deviceTwinClient.hasNext(twinQuery) && twins.size() < nubmerOfResult) {
                QueryCollectionResponse<DeviceTwinDevice> response = this.deviceTwinClient.next(twinQuery, options);
                response.getCollection().forEach(twin -> twins.put(twin.getDeviceId(), new DeviceTwinServiceModel(twin)));
            }
        } catch (IotHubException | IOException e) {
            throw new ExternalDependencyException("Unable to query device twin", e);
        }

        return twins;
    }
}
