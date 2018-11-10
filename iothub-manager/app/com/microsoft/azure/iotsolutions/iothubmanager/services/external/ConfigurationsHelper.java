package com.microsoft.azure.iotsolutions.iothubmanager.services.external;

import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.helpers.QueryConditionTranslator;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeploymentServiceModel;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeploymentType;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceGroup;
import com.microsoft.azure.sdk.iot.service.Configuration;
import com.sun.deploy.resources.Deployment;
import org.apache.commons.lang3.StringUtils;
import play.libs.Json;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static play.libs.Json.fromJson;

public class ConfigurationsHelper {

    private static final String DEPLOYMENT_TYPE_LABEL = "Type";
    private static final String DEPLOYMENT_NAME_LABEL = "Name";
    private static final String DEPLOYMENT_GROUP_ID_LABEL = "DeviceGroupId";
    private static final String DEPLOYMENT_GROUP_NAME_LABEL = "DeviceGroupName";
    private static final String DEPLOYMENT_PACKAGE_NAME_LABEL = "PackageName";

    public static final String RM_CREATED_LABEL = "RMDeployment";

    public static Configuration toHubConfiguration(final DeploymentServiceModel deployment) throws InvalidInputException {
        final String deploymentId = UUID.randomUUID().toString();
        final Configuration configuration = new Configuration(deploymentId);

        final String packageContent = deployment.getPackageContent();
        final Configuration pkgConfiguration = fromJson(Json.parse(packageContent), Configuration.class);
        configuration.setContent(pkgConfiguration.getContent());

        final DeviceGroup deploymentGroup = deployment.getDeviceGroup();
        final String dvcGroupQuery = deploymentGroup.getQuery();
        final String query = QueryConditionTranslator.ToQueryString(dvcGroupQuery);
        configuration.setTargetCondition(StringUtils.isNotBlank(query) ? query : "*");
        configuration.setPriority(deployment.getPriority());
        configuration.setEtag("");

        if(configuration.getLabels() == null) {
            configuration.setLabels(new HashMap<>());
        }
        final Map<String, String> labels = configuration.getLabels();

        // Required labels
        labels.put(DEPLOYMENT_TYPE_LABEL, deployment.getType().toString());
        labels.put(DEPLOYMENT_NAME_LABEL, deployment.getName());
        labels.put(DEPLOYMENT_GROUP_ID_LABEL, deploymentGroup.getId());
        labels.put(RM_CREATED_LABEL, Boolean.TRUE.toString());

        Map<String, String> systemMetrics = pkgConfiguration.getSystemMetrics().getQueries();
        if (systemMetrics != null)
        {
            configuration.getSystemMetrics().setQueries(systemMetrics);
        }

        Map<String, String> customMetrics = pkgConfiguration.getMetrics().getQueries();
        if (customMetrics != null)
        {
            configuration.getMetrics().setQueries(customMetrics);
        }

        // Add optional labels
        if (deploymentGroup.getName() != null) {
            labels.put(DEPLOYMENT_GROUP_NAME_LABEL, deploymentGroup.getName());
        }
        if (deployment.getPackageName() != null) {
            labels.put(DEPLOYMENT_PACKAGE_NAME_LABEL, deployment.getPackageName());
        }

        return configuration;
    }

    public static Boolean isEdgeDeployment(Configuration deployment)
    {
        if (deployment.getLabels() == null)
        {
            return false;
        }
        if (deployment.getLabels().get(DEPLOYMENT_TYPE_LABEL)
                .equals(DeploymentType.edgeManifest.toString()))
        {
            return true;
        }
        return false;
    }
}
