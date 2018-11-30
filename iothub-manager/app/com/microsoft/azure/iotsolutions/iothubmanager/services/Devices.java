// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.ResourceNotFoundException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.external.IStorageAdapterClient;
import com.microsoft.azure.iotsolutions.iothubmanager.services.helpers.QueryConditionTranslator;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.AuthenticationType;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DevicePropertyServiceModel;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceServiceListModel;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceServiceModel;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceTwinName;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.MethodParameterServiceModel;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.MethodResultServiceModel;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.TwinServiceListModel;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.TwinServiceModel;
import com.microsoft.azure.sdk.iot.service.Device;
import com.microsoft.azure.sdk.iot.service.RegistryManager;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceMethod;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceTwin;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceTwinDevice;
import com.microsoft.azure.sdk.iot.service.devicetwin.MethodResult;
import com.microsoft.azure.sdk.iot.service.devicetwin.QueryCollection;
import com.microsoft.azure.sdk.iot.service.devicetwin.QueryCollectionResponse;
import com.microsoft.azure.sdk.iot.service.devicetwin.QueryOptions;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubNotFoundException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import play.Logger;
import play.libs.Json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static java.util.concurrent.CompletableFuture.completedFuture;

public final class Devices implements IDevices {

    private static final Logger.ALogger log = Logger.of(Devices.class);

    private static final int MAX_GET_LIST = 1000;
    private static final String DevicesQueryPrefix = "SELECT * FROM devices";
    private static final String ModulesQueryPrefix = "SELECT * FROM devices.modules";
    private static final String DEVICES_CONNECTED_QUERY = "connectionState = 'Connected'";

    private final RegistryManager registry;
    private final DeviceTwin deviceTwinClient;
    private final DeviceMethod deviceMethodClient;
    private final String iotHubHostName;
    private final IStorageAdapterClient storageAdapterClient;

    @Inject
    public Devices(final IIoTHubWrapper ioTHubService,
                   final IStorageAdapterClient storageAdapterClient) throws Exception {
        this.storageAdapterClient = storageAdapterClient;
        this.registry = ioTHubService.getRegistryManagerClient();
        this.deviceTwinClient = ioTHubService.getDeviceTwinClient();
        this.deviceMethodClient = ioTHubService.getDeviceMethodClient();
        this.iotHubHostName = ioTHubService.getIotHubHostName();
    }

    public Devices(final RegistryManager registry,
                   final DeviceTwin deviceTwin,
                   final DeviceMethod deviceMethod,
                   final String iotHubHostName,
                   final IStorageAdapterClient storageAdapterClient) {
        this.storageAdapterClient = storageAdapterClient;
        this.registry = registry;
        this.deviceTwinClient = deviceTwin;
        this.deviceMethodClient = deviceMethod;
        this.iotHubHostName = iotHubHostName;
    }

    public DeviceTwinName getDeviceTwinNames()
            throws ExternalDependencyException, ExecutionException, InterruptedException {
        CompletableFuture<DeviceServiceListModel> twinNamesTask = this.queryAsync
                ("", "").toCompletableFuture();
        DeviceServiceListModel model = twinNamesTask.get();
        DeviceTwinName twinNames = model.toDeviceTwinNames();
        return twinNames;
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
                            final boolean isEdgeConnectedDevice = this.doesDeviceHaveConnectedModules(id)
                                                                      .toCompletableFuture()
                                                                      .get();
                            return new DeviceServiceModel(device,
                                                          new TwinServiceModel(twin),
                                                          this.iotHubHostName,
                                                          isEdgeConnectedDevice);
                        } catch (IOException | IotHubException e) {
                            String message = String.format("Unable to retrieve device twin by id: %s", id);
                            log.error(message, e);
                            throw new CompletionException(new ExternalDependencyException(message, e));
                        } catch (InterruptedException | ExecutionException | ExternalDependencyException e) {
                            String message = "Unable to check edge connectivity for device: " + id;
                            log.error(message, e);
                            throw new CompletionException(message, e);
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
            // TODO: Retrieving devices doesn't actually make use of query / continuationToken appropriately
            // Switch to DeviceTwin.queryTwin

            // normally we need deviceTwins for all devices to show device list
            return this.registry.getDevicesAsync(MAX_GET_LIST)
                    .handle((devices, error) -> {
                        if (error != null) {
                            String message = String.format("Unable to get device by query: %s", query);
                            log.error(message, error);
                            throw new CompletionException(new ExternalDependencyException(message, error));
                        }

                        try {
                            final Pair<HashMap<String, TwinServiceModel>, String> twins = getTwinByQueryAsync(
                                    DevicesQueryPrefix,
                                    QueryConditionTranslator.ToQueryString(query),
                                    continuationToken,
                                    MAX_GET_LIST);

                            final Map<String, TwinServiceModel> twinsMap = twins.getLeft();
                            final String responseContinuationToken = twins.getRight();

                            devices = devices.stream()
                                             .filter(device -> twinsMap.containsKey(device.getDeviceId()))
                                             .collect(Collectors.toCollection(ArrayList::new));
                            final Set<String> connectedEdgeDevices = this.getConnectedEdgeDevices(devices,
                                    twinsMap).toCompletableFuture().get();

                            ArrayList<DeviceServiceModel> deviceList = new ArrayList<>();
                            for (Device azureDevice : devices) {
                                String deviceId = azureDevice.getDeviceId();
                                deviceList.add(new DeviceServiceModel(
                                        azureDevice,
                                        twinsMap.get(deviceId),
                                        this.iotHubHostName,
                                        connectedEdgeDevices.contains(deviceId)));
                            }

                            return new DeviceServiceListModel(deviceList, responseContinuationToken);
                        } catch (InvalidInputException | ExternalDependencyException e) {
                            String message = String.format("Unable to get device twin by query: %s", query);
                            log.error(message, e);
                            throw new CompletionException(message, e);
                        } catch (InterruptedException | ExecutionException e) {
                            String message = String.format("Unable to get device twin by query: %s. " +
                                    "Completion exception in getting edge connection status.", query);
                            log.error(message, e);
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
        if (device.getIsEdgeDevice() &&
                device.getAuthentication() != null &&
                !device.getAuthentication().getAuthenticationType().equals(AuthenticationType.Sas)) {
            throw new InvalidInputException("Edge devices only support symmetric key authentication.");
        }

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
                            TwinServiceModel twinServiceModel = device.getTwin();
                            DeviceTwinDevice azureTwin = new DeviceTwinDevice(device.getId());
                            if (twinServiceModel == null || twinServiceModel.getETag() == null) {
                                this.deviceTwinClient.getTwin(azureTwin);
                                return new DeviceServiceModel(azureDevice,
                                        new TwinServiceModel(azureTwin), this.iotHubHostName);
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

    /**
     * Returns a CompletableFuture of DeviceServiceModel after a  DeviceServiceModel
     * has been added or updated to IOT hub.
     * <p>
     * This method also adds deviceProperties to the cache in cosmosDB using a callback
     * method, after the deviceModel has been created or updated.
     *
     * @param id                     device id of type string
     * @param device                 device of type DeviceServiceModel
     * @param devicePropertyCallBack devicePropertyCallBack of type DevicePropertyCallBack
     * @return a CompletableFuture of DeviceServiceModel
     */
    public CompletionStage<DeviceServiceModel> createOrUpdateAsync(
            final String id, final DeviceServiceModel device, DevicePropertyCallBack devicePropertyCallBack)
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
                                return new DeviceServiceModel(azureDevice,
                                        new TwinServiceModel(twin), this.iotHubHostName);
                            } else {
                                this.deviceTwinClient.updateTwin(device.getTwin().toDeviceTwinDevice());
                                DevicePropertyServiceModel model = new DevicePropertyServiceModel();
                                if (device.getTwin() != null && device.getTwin().getTags() != null) {
                                    model.setTags(new HashSet<>(device.getTwin().getTags().keySet()));
                                }
                                if (device.getTwin() != null && device.getTwin().getProperties() != null &&
                                        device.getTwin().getProperties().getReported() != null) {
                                    model.setReported(new HashSet<>(
                                            device.getTwin().getProperties().getReported().keySet()));
                                }
                                // Update the deviceProperties cache, no need to wait
                                CompletionStage unused = devicePropertyCallBack.updateCache(model);
                                return new DeviceServiceModel(azureDevice, device.getTwin(), this.iotHubHostName);
                            }
                        } catch (IOException | IotHubException e) {
                            String message = String.format("Unable to get or update twin of device: %s", id);
                            log.error(message, e);
                            throw new CompletionException(
                                    new ExternalDependencyException(message, e));
                        } catch (Exception e) {
                            throw new CompletionException(new Exception("Unable to update cache", e));
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
                            throw new CompletionException(
                                    new ResourceNotFoundException("Unable to delete non-exist device: " + id, error));
                        } else {
                            throw new CompletionException(
                                    new ExternalDependencyException("Unable to delete device" + id, error));
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

    /**
     * {@inheritDoc}
     */
    public CompletionStage<TwinServiceModel> getModuleTwinAsync(String deviceId, String moduleId)
            throws ExternalDependencyException, InvalidInputException {
        if (StringUtils.isEmpty(deviceId)) {
            throw new InvalidInputException("DeviceId must be provided");
        }

        if (StringUtils.isEmpty(moduleId)) {
            throw new InvalidInputException("ModuleId must be provided");
        }

        final String query = String.format("deviceId = '%s' and moduleId = '%s'", deviceId, moduleId);

        try {
            final Pair<HashMap<String, TwinServiceModel>, String> twinsResult = this.getTwinByQueryAsync(
                    ModulesQueryPrefix,
                    query,
                    StringUtils.EMPTY,
                    1);

            final Map<String, TwinServiceModel> twinsMap = twinsResult.getLeft();

            if (MapUtils.isNotEmpty(twinsMap)) {
                return completedFuture(twinsMap.get(deviceId));
            }
        } catch (ExternalDependencyException e) {
            final String message = String.format("Unable to get device twin by query: %s", query);
            log.error(message, e);
            throw e;
        }

        throw new CompletionException(new ResourceNotFoundException(String.format("Unable to get devices " +
                "with query %s", query)));
    }

    /**
     * {@inheritDoc}
     */
    public CompletionStage<TwinServiceListModel> getModuleTwinsByQueryAsync(String query, String
            continuationToken) throws ExternalDependencyException {
        try {
            final Pair<HashMap<String, TwinServiceModel>, String> twinsResult = this.getTwinByQueryAsync(
                    ModulesQueryPrefix,
                    QueryConditionTranslator.ToQueryString(query),
                    continuationToken,
                    MAX_GET_LIST);

            final String newContinuationToken = twinsResult.getRight();
            final Map<String, TwinServiceModel> twinsMap = twinsResult.getLeft();
            final List<TwinServiceModel> listOfTwins = new ArrayList<>(twinsMap.values());

            return completedFuture(new TwinServiceListModel(listOfTwins, newContinuationToken));
        } catch (InvalidInputException e) {
            final String message = String.format("Unable to get device twin by query: %s", query);
            log.error(message, e);
            throw new CompletionException(message, e);
        } catch (ExternalDependencyException e) {
            final String message = String.format("Unable to get device twin by query: %s", query);
            log.error(message, e);
            throw e;
        }
    }

    private Pair<HashMap<String, TwinServiceModel>, String> getTwinByQueryAsync(
            final String queryPrefix, final String query, String continuationToken, int numberOfResults)
            throws ExternalDependencyException {
        final String fullQuery = query.isEmpty() ? queryPrefix : String.format("%s where %s", queryPrefix,
                query);
        final QueryOptions options = new QueryOptions();
        if (StringUtils.isNotEmpty(continuationToken)) {
            options.setContinuationToken(continuationToken);
        }

        final HashMap<String, TwinServiceModel> twins = new HashMap<>();
        String responseContinuationToken = continuationToken;
        try {
            QueryCollection twinQuery = this.deviceTwinClient.queryTwinCollection(fullQuery);
            while (this.deviceTwinClient.hasNext(twinQuery) && twins.size() < numberOfResults) {
                QueryCollectionResponse<DeviceTwinDevice> response = this.deviceTwinClient.next(twinQuery, options);
                responseContinuationToken = response.getContinuationToken();
                response.getCollection().
                        forEach(twin -> twins.put(twin.getDeviceId(), new TwinServiceModel(twin)));
            }
        } catch (IotHubException | IOException e) {
            throw new ExternalDependencyException("Unable to query device twin", e);
        }

        log.info(String.format("Found %d devices for query %s starting at continuationToken %s",
                twins.size(), fullQuery, continuationToken));
        return new ImmutablePair<>(twins, responseContinuationToken);
    }

    /**
     * Retrieves the list of edge devices which are reporting as connected based on
     * connectivity of their modules. If any of the modules are connected than the edge device
     * should report as connected.
     * @param devicesList The list of devices to check
     * @param twinsMap Map of associated twins for those devices
     * @return Set of edge device ids
     */
    private CompletionStage<Set<String>> getConnectedEdgeDevices(List<Device> devicesList,
                                                                 Map<String, TwinServiceModel> twinsMap) throws ExternalDependencyException {
        final boolean hasEdgeDevices = devicesList.stream().anyMatch(dvc -> dvc.getCapabilities() != null
                && dvc.getCapabilities().isIotEdge());
        final boolean hasEdgeTwins = twinsMap.values().stream().anyMatch(TwinServiceModel::getIsEdgeDevice);
        if (!hasEdgeDevices && !hasEdgeTwins) {
            return CompletableFuture.completedFuture(new HashSet<String>());
        }

        return this.getDevicesWithConnectedModules().thenApplyAsync(connectedModules -> {
            return devicesList.stream()
                    .filter(device -> (device.getCapabilities() != null &&
                    device.getCapabilities().isIotEdge()) || twinsMap.get(device.getDeviceId()).getIsEdgeDevice())
                    .filter(edgeDvc -> connectedModules.contains(edgeDvc.getDeviceId()))
                    .map(connectedEdgeDvc -> connectedEdgeDvc.getDeviceId())
                    .collect(Collectors.toCollection(HashSet::new));
        });
    }

    /**
     * Retrieves the set of devices that have at least one module connected.
     * @return Set of devices which are listed as connected
     */
    private CompletionStage<Set<String>> getDevicesWithConnectedModules() throws ExternalDependencyException {
        return this.getModuleTwinsByQueryAsync(DEVICES_CONNECTED_QUERY, "").thenApplyAsync(twins -> {
                    return twins.getItems().stream().map(TwinServiceModel::getDeviceId)
                            .distinct()
                            .collect(Collectors.toCollection(HashSet::new));
                }
        );
    }

    /**
     * Checks if a single device has connected modules
     * @param deviceId Device Id to query
     * @return True if one of the modules for this device is connected.
     */
    private CompletionStage<Boolean> doesDeviceHaveConnectedModules(String deviceId) throws ExternalDependencyException {
        String query = String.format("deviceId='%s' AND %s", deviceId, DEVICES_CONNECTED_QUERY);
        return this.getModuleTwinsByQueryAsync(query, "").thenApplyAsync(twins -> {
                return CollectionUtils.isNotEmpty(twins.getItems());
            }
        );
    }
}
