// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.InvalidConfigurationException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.ResourceNotFoundException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.external.ConfigurationsHelper;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.*;
import com.microsoft.azure.sdk.iot.service.Configuration;
import com.microsoft.azure.sdk.iot.service.RegistryManager;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceTwin;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceTwinDevice;
import com.microsoft.azure.sdk.iot.service.devicetwin.Query;
import com.microsoft.azure.sdk.iot.service.devicetwin.SqlQuery;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubBadFormatException;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubNotFoundException;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import play.Logger;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public final class Deployments implements IDeployments {

    private static final Logger.ALogger log = Logger.of(Deployments.class);
    private static final int MAX_DEPLOYMENTS = 20;

    private static final String DEVICE_GROUP_ID_PARAM = "deviceGroupId";
    private static final String DEVICE_GROUP_NAME_PARAM = "deviceGroupName";
    private static final String DEVICE_GROUP_QUERY_PARAM = "deviceGroupQuery";
    private static final String NAME_PARAM = "name";
    private static final String CONFIG_TYPE_PARAM = "configType";
    private static final String PACKAGE_CONTENT_PARAM = "packageContent";

    private static final String SCHEMA_VERSION = "schemaVersion";

    private final RegistryManager registry;
    private final DeviceTwin deviceTwin;
    private final String ioTHubHostName;

    @Inject
    public Deployments(final IIoTHubWrapper ioTHubService) throws Exception {
        this.registry = ioTHubService.getRegistryManagerClient();
        this.deviceTwin = ioTHubService.getDeviceTwinClient();
        this.ioTHubHostName = ioTHubService.getIotHubHostName();
    }

    public Deployments(final String ioTHubHostName,
                       final DeviceTwin deviceTwin,
                       final RegistryManager registry) {
        this.registry = registry;
        this.deviceTwin = deviceTwin;
        this.ioTHubHostName = ioTHubHostName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletionStage<DeploymentServiceListModel> listAsync() throws
            ExternalDependencyException {
        try {
            final List<Configuration> deployments = this.registry.getConfigurations(MAX_DEPLOYMENTS);

            if (deployments == null) {
                throw new CompletionException(
                        new ResourceNotFoundException(String.format("No deployments found for %s hub.", this
                                .ioTHubHostName)));
            }

            final List<DeploymentServiceModel> serviceModelDeployments =
                    deployments.stream().filter(Deployments::deploymentMadeByRM)
                            .map(config -> {
                                try {
                                    return new DeploymentServiceModel(config);
                                } catch (InvalidInputException | InvalidConfigurationException e) {
                                    throw new CompletionException(e);
                                }
                            })
                            .sorted(Comparator.comparing(DeploymentServiceModel::getName))
                            .collect(Collectors.toList());
            return CompletableFuture.supplyAsync(() -> new DeploymentServiceListModel(serviceModelDeployments));
        } catch (IOException | IotHubException e) {
            final String message = "Unable to list deployments";
            log.error(message, e);
            throw new ExternalDependencyException(message, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletionStage<DeploymentServiceModel> getAsync(String id, boolean includeDeviceStatus) throws
            ExternalDependencyException, InvalidConfigurationException {
        try {
            final Configuration deployment = this.registry.getConfiguration(id);

            if (deployment == null) {
                throw new CompletionException(
                    new ResourceNotFoundException(String.format("No deployment with id %s found for %s hub.",
                                                                id, this.ioTHubHostName)));
            }

            final DeploymentServiceModel result;
            try {
                result = new DeploymentServiceModel(deployment);
            } catch (InvalidInputException e) {
                throw new CompletionException(e);
            }

            Map<String, DeploymentStatus> deviceStatuses = null;

            try {
                deviceStatuses = this.getDeviceStatuses(deployment);
            } catch(Exception ex) {
                log.error("Unable to retrieve device statuses for deployment " + id, ex);
            }

            if (includeDeviceStatus) {
                result.getDeploymentMetrics().setDeviceStatuses(deviceStatuses);
            }

            result.getDeploymentMetrics().setDeviceMetrics(this.calculateDeviceMetrics(deviceStatuses));

            return CompletableFuture.supplyAsync(() -> result);
        } catch (IotHubNotFoundException e) {
            throw new CompletionException(
                    new ResourceNotFoundException(String.format("No deployment with id %s found for %s hub.",
                            id, this.ioTHubHostName)));
        } catch (IOException | IotHubException e) {
            final String message = "Unable to list deployments";
            log.error(message, e);
            throw new ExternalDependencyException(message, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletionStage<DeploymentServiceModel> createAsync(DeploymentServiceModel deployment) throws
            InvalidInputException, ExternalDependencyException, InvalidConfigurationException {

        verifyDeploymentParameter(DEVICE_GROUP_ID_PARAM, deployment.getDeviceGroup().getId());
        verifyDeploymentParameter(DEVICE_GROUP_NAME_PARAM, deployment.getDeviceGroup().getId());
        verifyDeploymentParameter(DEVICE_GROUP_QUERY_PARAM, deployment.getDeviceGroup().getQuery());
        verifyDeploymentParameter(NAME_PARAM, deployment.getName());
        verifyDeploymentParameter(PACKAGE_CONTENT_PARAM, deployment.getPackageContent());

        if (deployment.getPackageType().equals(PackageType.deviceConfiguration)) {
            verifyDeploymentParameter(CONFIG_TYPE_PARAM, deployment.getConfigType());
        }

        if (deployment.getPriority() < 0) {
            throw new InvalidInputException("Invalid input. A priority should be provided greater than 0.");
        }

        try {
            final Configuration config = ConfigurationsHelper.toHubConfiguration(deployment);
            final Configuration result = this.registry.addConfiguration(config);
            return CompletableFuture.completedFuture(new DeploymentServiceModel(result));
        }
        catch (IotHubBadFormatException e) {
            log.error("Unable to create deployment. Verify the format of your query.", e);
            throw new InvalidInputException(e.toString());
        }
        catch (InvalidInputException e) {
            final String message = "Unable to create deployment. Invalid group or package information " +
                    "provided";
            log.error(message, e);
            throw new CompletionException(message, e);
        }
        catch (IotHubException | IOException e) {
            final String message = "Unable to create deployment when communicating with the hub.";
            log.error(message, e);
            throw new ExternalDependencyException(message, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletionStage<Boolean> deleteAsync(String id) throws ExternalDependencyException, ResourceNotFoundException {
        try {
            this.registry.removeConfiguration(id);
            return CompletableFuture.completedFuture(true);
        } catch (IotHubNotFoundException e) {
            throw new ResourceNotFoundException(String.format("No deployment with id %s found for %s hub.",
                            id, this.ioTHubHostName));
        } catch (IOException|IotHubException e) {
            final String message = "Unable to delete deployment with id: " + id;
            log.error(message, e);
            throw new ExternalDependencyException(message, e);
        }
    }

    /**
     * Does 3 queries to the IoTHub to get the status of the deployment per device.
     *
     * @param deployment - Deployment id for the deployment to query.
     * @return Map of deviceId to the {@link DeploymentStatus}.
     */
    private Map<String, DeploymentStatus> getDeviceStatuses(Configuration deployment) throws
            IOException, InvalidConfigurationException {

        String packageType = null;
        if (ConfigurationsHelper.isEdgeDeployment(deployment)) {
            packageType = PackageType.edgeManifest.toString();
        } else {
            packageType = PackageType.deviceConfiguration.toString();
        }

        String configType = deployment.getLabels().getOrDefault(
                                                        ConfigurationsHelper.CONFIG_TYPE_LABEL,
                                                        StringUtils.EMPTY);

        Map<DeviceStatusQueries.QueryType, String> queries = DeviceStatusQueries.getQueries(packageType, configType);
        Map<String,DeploymentStatus> deviceStatuses = new HashMap<>();
        String deploymentId = deployment.getId();

        final Set<String> appliedDeviceIds = this.getDevicesInQuery(
                queries.get(DeviceStatusQueries.QueryType.APPLIED),
                deploymentId);

        if (!(ConfigurationsHelper.isEdgeDeployment(deployment)) &&
                !(configType.equals(ConfigType.firmware.toString()))) {
            for (String devices : appliedDeviceIds) {
                deviceStatuses.put(devices, DeploymentStatus.Unknown);
            }

            return deviceStatuses;
        }

        final Set<String> successfulDeviceIds = this.getDevicesInQuery(
                queries.get(DeviceStatusQueries.QueryType.SUCCESSFUL), deploymentId);

        final Set<String> failedDeviceIds = this.getDevicesInQuery(
                queries.get(DeviceStatusQueries.QueryType.FAILED), deploymentId);

        for (String successfulDevice : successfulDeviceIds) {
            deviceStatuses.put(successfulDevice, DeploymentStatus.Succeeded);
        }
        for (String failedDevice : failedDeviceIds) {
            deviceStatuses.put(failedDevice, DeploymentStatus.Failed);
        }
        for (String device : appliedDeviceIds) {
            if (!successfulDeviceIds.contains(device) && !failedDeviceIds.contains(device)) {
                deviceStatuses.put(device, DeploymentStatus.Pending);
            }
        }
        return deviceStatuses;
    }

    private Set<String> getDevicesInQuery(String hubQuery, String deploymentId) throws IOException {
        final String query = String.format(hubQuery, deploymentId);
        final Query twinQuery;
        final Set<String> deviceIds = new HashSet<>();

        try {
            // TODO: Add pagination
            twinQuery = deviceTwin.queryTwin(query);
        } catch (IotHubException | IOException ex) {
            log.error(String.format("Unable to get devices with query %s in deployment %s", query,
                    deploymentId), ex);
            return deviceIds;
        }

        try {
            while (twinQuery.hasNext()) {
                final DeviceTwinDevice device = deviceTwin.getNextDeviceTwin(twinQuery);
                deviceIds.add(device.getDeviceId());
            }
        }
        catch (Exception ex) {
            log.error("Error getting status of devices in query " + query.toString());
        }
        return deviceIds;
    }

    private static boolean deploymentMadeByRM(Configuration conf) {
        return conf.getLabels() != null &&
                conf.getLabels().containsKey(ConfigurationsHelper.RM_CREATED_LABEL) &&
                BooleanUtils.toBoolean(conf.getLabels().get(ConfigurationsHelper.RM_CREATED_LABEL));
    }


    private static void verifyDeploymentParameter(final String argumentName, final String argumentValue)
            throws InvalidInputException {
        if (StringUtils.isEmpty(argumentValue)) {
            throw new InvalidInputException("Invalid input. Must provide a value to " +
                    argumentName);
        }
    }


    private Map<DeploymentStatus, Long> calculateDeviceMetrics(Map<String, DeploymentStatus> deviceStatuses) {
        if (deviceStatuses == null) {
            return null;
        }

        Map<DeploymentStatus, Long> deviceMetrics = new HashMap<DeploymentStatus, Long>();
        deviceMetrics.put(DeploymentStatus.Succeeded, deviceStatuses.values().stream().filter(item ->
                item == DeploymentStatus.Succeeded).count());

        deviceMetrics.put(DeploymentStatus.Failed, deviceStatuses.values().stream().filter(item ->
                item == DeploymentStatus.Failed).count());

        deviceMetrics.put(DeploymentStatus.Pending, deviceStatuses.values().stream().filter(item ->
                item == DeploymentStatus.Pending).count());

        return deviceMetrics;
    }
}
