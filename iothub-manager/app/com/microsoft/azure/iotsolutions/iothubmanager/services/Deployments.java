// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.ResourceNotFoundException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.helpers.QueryConditionTranslator;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeploymentServiceListModel;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeploymentServiceModel;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeploymentStatus;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceGroup;
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
import play.libs.Json;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static play.libs.Json.fromJson;

public final class Deployments implements IDeployments {

    private static final Logger.ALogger log = Logger.of(Deployments.class);
    private static final int MAX_DEPLOYMENTS = 20;

    private static final String DEPLOYMENT_NAME_LABEL = "Name";
    private static final String DEPLOYMENT_GROUP_ID_LABEL = "DeviceGroupId";
    private static final String DEPLOYMENT_GROUP_NAME_LABEL = "DeviceGroupName";
    private static final String DEPLOYMENT_PACKAGE_NAME_LABEL = "PackageName";
    private static final String RM_CREATED_LABEL = "RMDeployment";

    private static final String DEVICE_GROUP_ID_PARAM = "deviceGroupId";
    private static final String DEVICE_GROUP_QUERY_PARAM = "deviceGroupQuery";
    private static final String NAME_PARAM = "name";
    private static final String PACKAGE_CONTENT_PARAM = "packageContent";

    private static final String SCHEMA_VERSION = "schemaVersion";
    private static final String APPLIED_DEVICES_QUERY =
            "moduleId = '$edgeAgent' and configurations.[[%s]].status = 'Applied'";
    private static final String SUCCESSFUL_DEVICES_QUERY = APPLIED_DEVICES_QUERY +
            " and properties.desired.$version = properties.reported.lastDesiredVersion" +
            " and properties.reported.lastDesiredStatus.code = 200";
    private static final String FAILED_DEVICES_QUERY = APPLIED_DEVICES_QUERY +
            " and properties.desired.$version = properties.reported.lastDesiredVersion" +
            " and properties.reported.lastDesiredStatus.code != 200";

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
                                } catch (InvalidInputException e) {
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
            ExternalDependencyException {
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

            if (includeDeviceStatus) {
                Map<String, DeploymentStatus> deviceStatuses = null;

                try {
                    deviceStatuses = this.getDeviceStatuses(id);
                } catch(Exception ex) {
                    log.error("Unable to retrieve device statuses for deployment " + id, ex);
                }

                result.getDeploymentMetrics().setDeviceStatuses(deviceStatuses);
            }
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
            InvalidInputException, ExternalDependencyException {
        this.verifyDeploymentParameter(DEVICE_GROUP_ID_PARAM, deployment.getDeviceGroup().getId());
        this.verifyDeploymentParameter(DEVICE_GROUP_QUERY_PARAM, deployment.getDeviceGroup().getQuery());
        this.verifyDeploymentParameter(NAME_PARAM, deployment.getName());
        this.verifyDeploymentParameter(PACKAGE_CONTENT_PARAM, deployment.getPackageContent());

        if (deployment.getPriority() < 0) {
            throw new InvalidInputException("Invalid input. A priority should be provided greater than 0.");
        }

        try {
            final Configuration edgeConfig = createEdgeConfiguration(deployment);
            final Configuration result = this.registry.addConfiguration(edgeConfig);
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
     * @param deploymentId - Deployment id for the deployment to query.
     * @return Map of deviceId to the {@link DeploymentStatus}.
     */
    private Map<String, DeploymentStatus> getDeviceStatuses(String deploymentId) throws IOException {
        final Set<String> appliedDeviceIds = this.getDevicesInQuery(APPLIED_DEVICES_QUERY, deploymentId);
        final Set<String> successfulDeviceIds = this.getDevicesInQuery(SUCCESSFUL_DEVICES_QUERY, deploymentId);
        final Set<String> failedDeviceIds = this.getDevicesInQuery(FAILED_DEVICES_QUERY, deploymentId);
        Map<String,DeploymentStatus> deviceStatuses = new HashMap<>();

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

    private void verifyDeploymentParameter(final String argumentName, final String argumentValue)
            throws InvalidInputException {
        if (StringUtils.isEmpty(argumentValue)) {
            throw new InvalidInputException("Invalid input. Must provide a value to " +
                    argumentName);
        }
    }

    private Set<String> getDevicesInQuery(String hubQuery, String deploymentId) throws IOException {
        final String query = String.format(hubQuery, deploymentId);
        final SqlQuery sqlQuery = SqlQuery.createSqlQuery("*", SqlQuery.FromType.MODULES, query, null);
        final Query twinQuery;
        final Set<String> deviceIds = new HashSet<>();

        try {
            // TODO: Add pagination
            twinQuery = deviceTwin.queryTwin(sqlQuery.getQuery());
        } catch (IotHubException ex) {
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
            log.error("Error getting status of devices in query " + sqlQuery.toString());
        }
        return deviceIds;
    }

    private static Configuration createEdgeConfiguration(final DeploymentServiceModel deployment) throws InvalidInputException {
        final String deploymentId = UUID.randomUUID().toString();
        final Configuration edgeConfiguration = new Configuration(deploymentId);

        final String packageContent = deployment.getPackageContent();
        final Configuration pkgConfiguration = fromJson(Json.parse(packageContent), Configuration.class);
        edgeConfiguration.setContent(pkgConfiguration.getContent());

        final DeviceGroup deploymentGroup = deployment.getDeviceGroup();
        final String dvcGroupQuery = deploymentGroup.getQuery();
        final String query = QueryConditionTranslator.ToQueryString(dvcGroupQuery);
        edgeConfiguration.setTargetCondition(StringUtils.isNotBlank(query) ? query : "*");
        edgeConfiguration.setPriority(deployment.getPriority());
        edgeConfiguration.setEtag("");

        if(edgeConfiguration.getLabels() == null) {
            edgeConfiguration.setLabels(new HashMap<>());
        }
        final Map<String, String> labels = edgeConfiguration.getLabels();

        // Required labels
        labels.put(DEPLOYMENT_NAME_LABEL, deployment.getName());
        labels.put(DEPLOYMENT_GROUP_ID_LABEL, deploymentGroup.getId());
        labels.put(RM_CREATED_LABEL, Boolean.TRUE.toString());

        // Add optional labels
        if (deploymentGroup.getName() != null) {
            labels.put(DEPLOYMENT_GROUP_NAME_LABEL, deploymentGroup.getName());
        }
        if (deployment.getPackageName() != null) {
            labels.put(DEPLOYMENT_PACKAGE_NAME_LABEL, deployment.getPackageName());
        }

        return edgeConfiguration;
    }

    private static boolean deploymentMadeByRM(Configuration conf) {
        return conf.getLabels() != null &&
                conf.getLabels().containsKey(RM_CREATED_LABEL) &&
                BooleanUtils.toBoolean(conf.getLabels().get(RM_CREATED_LABEL));
    }
}
