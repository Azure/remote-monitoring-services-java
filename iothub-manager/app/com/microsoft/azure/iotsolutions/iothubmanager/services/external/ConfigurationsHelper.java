// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.external;

import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.helpers.QueryConditionTranslator;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeploymentServiceModel;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.PackageType;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceGroup;
import com.microsoft.azure.sdk.iot.service.Configuration;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import play.libs.Json;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static play.libs.Json.fromJson;

public class ConfigurationsHelper {

    public static final String PACKAGE_TYPE_LABEL = "Type";
    public static final String CONFIG_TYPE_LABEL = "ConfigType";
    public static final String DEPLOYMENT_NAME_LABEL = "Name";
    public static final String DEPLOYMENT_GROUP_ID_LABEL = "DeviceGroupId";
    public static final String DEPLOYMENT_GROUP_NAME_LABEL = "DeviceGroupName";
    public static final String DEPLOYMENT_PACKAGE_NAME_LABEL = "PackageName";
    public static final String RM_CREATED_LABEL = "RMDeployment";

    public static Configuration toHubConfiguration(final DeploymentServiceModel model) throws InvalidInputException {

        final String packageContent = model.getPackageContent();
        final Configuration pkgConfiguration = fromJson(Json.parse(packageContent), Configuration.class);

        if (model.getPackageType().equals(PackageType.edgeManifest) &&
                pkgConfiguration.getContent() != null &&
                pkgConfiguration.getContent().getDeviceContent() != null &&
                pkgConfiguration.getContent().getDeviceContent().size() != 0) {

            throw new InvalidInputException("Deployment type does not match with package contents.");

        } else if (model.getPackageType().equals(PackageType.deviceConfiguration) &&
                pkgConfiguration.getContent() != null &&
                MapUtils.isNotEmpty(pkgConfiguration.getContent().getModulesContent())) {

            throw new InvalidInputException("Deployment type does not match with package contents.");
        }

        final String deploymentId = UUID.randomUUID().toString();
        final Configuration configuration = new Configuration(deploymentId);
        configuration.setContent(pkgConfiguration.getContent());
        final DeviceGroup deploymentGroup = model.getDeviceGroup();
        final String dvcGroupQuery = deploymentGroup.getQuery();
        final String query = QueryConditionTranslator.ToQueryString(dvcGroupQuery);
        configuration.setTargetCondition(StringUtils.isNotBlank(query) ? query : "*");
        configuration.setPriority(model.getPriority());
        configuration.setEtag("");

        final HashMap<String, String> labels = (pkgConfiguration.getLabels() != null) ?
                pkgConfiguration.getLabels() : new HashMap<>();

        // Required labels
        labels.put(PACKAGE_TYPE_LABEL, model.getPackageType().toString());
        labels.put(CONFIG_TYPE_LABEL, model.getConfigType().toString());
        labels.put(DEPLOYMENT_NAME_LABEL, model.getName());
        labels.put(DEPLOYMENT_GROUP_ID_LABEL, deploymentGroup.getId());
        labels.put(RM_CREATED_LABEL, Boolean.TRUE.toString());

        // Add optional labels
        if (deploymentGroup.getName() != null) {
            labels.put(DEPLOYMENT_GROUP_NAME_LABEL, deploymentGroup.getName());
        }
        if (model.getPackageName() != null) {
            labels.put(DEPLOYMENT_PACKAGE_NAME_LABEL, model.getPackageName());
        }

        if (labels != null) {
            configuration.setLabels(labels);
        }

        Map<String, String> customMetrics = pkgConfiguration.getMetrics().getQueries();
        if (customMetrics != null) {
            configuration.getMetrics().setQueries(substituteDeploymentIdIfPresent(
                                                                            customMetrics,
                                                                            deploymentId));
        }

        return configuration;
    }

    public static Boolean isEdgeDeployment(Configuration deployment) {

        if (deployment.getLabels() == null) {
            return false;
        }

        if (deployment.getLabels().get(PACKAGE_TYPE_LABEL)
                .equals(PackageType.edgeManifest.toString())) {
            return true;
        }
        return false;
    }

    // Replaces DeploymentId, if present, in the custom metrics query
    public static Map<String, String> substituteDeploymentIdIfPresent(
            Map<String, String> customMetrics,
            String deploymentId) {
        final String statusClause = "configurations\\.\\[\\[[a-zA-Z0-9\\-]+\\]\\]\\.status";
        String updatedStatusClause = "configurations.[[" + deploymentId + "]].status";

        for(Map.Entry<String, String> query : customMetrics.entrySet()) {
            customMetrics.put(query.getKey(), query.getValue().replaceAll(
                                                                statusClause,
                                                                updatedStatusClause));
        }

        return customMetrics;
    }
}
