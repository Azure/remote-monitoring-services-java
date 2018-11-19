// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.models;

import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.external.ConfigurationsHelper;
import com.microsoft.azure.sdk.iot.service.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class DeploymentServiceModel {

    private final static String DATE_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private String id;
    private String name;
    private DeviceGroup deviceGroup;
    private String packageContent;
    private String packageName;
    private int priority;
    private String createdDateTimeUtc;
    private DeploymentType deploymentType;
    private String configType;
    private DeploymentMetrics deploymentMetrics;

    public DeploymentServiceModel(final String name,
                                  final DeviceGroup deviceGroup,
                                  final String packageContent,
                                  final String packageName,
                                  final int priority,
                                  final DeploymentType deploymentType,
                                  final String configType) {
        this.deviceGroup = deviceGroup;
        this.packageContent = packageContent;
        this.name = name;
        this.packageName = packageName;
        this.priority = priority;
        this.deploymentType = deploymentType;
        this.configType = configType;
    }

    public DeploymentServiceModel(Configuration deployment) throws InvalidInputException {
        if (StringUtils.isEmpty(deployment.getId())) {
            throw new InvalidInputException("Invalid id provided");
        }

        if (!deployment.getLabels().containsKey(ConfigurationsHelper.DEPLOYMENT_GROUP_ID_LABEL)) {
            throw new InvalidInputException("Configuration is missing necessary label "
                    + ConfigurationsHelper.DEPLOYMENT_GROUP_ID_LABEL);
        }

        if (!deployment.getLabels().containsKey(ConfigurationsHelper.DEPLOYMENT_NAME_LABEL)) {
            throw new InvalidInputException("Configuration is missing necessary label "
                    + ConfigurationsHelper.DEPLOYMENT_NAME_LABEL);
        }

        this.id = deployment.getId();
        this.name = deployment.getLabels().get(ConfigurationsHelper.DEPLOYMENT_NAME_LABEL);

        String deviceGroupId = deployment.getLabels().get(ConfigurationsHelper.DEPLOYMENT_GROUP_ID_LABEL);
        String deviceGroupName = StringUtils.EMPTY;
        if (deployment.getLabels().containsKey(ConfigurationsHelper.DEPLOYMENT_GROUP_NAME_LABEL)) {
            deviceGroupName = deployment.getLabels().get(ConfigurationsHelper.DEPLOYMENT_GROUP_NAME_LABEL);
        }
        this.deviceGroup = new DeviceGroup(deviceGroupId, deviceGroupName, null);

        this.packageName = StringUtils.EMPTY;
        if (deployment.getLabels().containsKey(ConfigurationsHelper.DEPLOYMENT_PACKAGE_NAME_LABEL)) {
            this.packageName = deployment.getLabels().get(ConfigurationsHelper.DEPLOYMENT_PACKAGE_NAME_LABEL);
        }

        this.createdDateTimeUtc =  this.formatDateTimeToUTC(deployment.getCreatedTimeUtc());
        this.priority = deployment.getPriority();

        if (deployment.getLabels().containsKey(ConfigurationsHelper.DEPLOYMENT_TYPE_LABEL) &&
            !(StringUtils.isBlank(deployment.getLabels().get(ConfigurationsHelper.DEPLOYMENT_TYPE_LABEL))))
        {
            if (deployment.getLabels().containsValue(DeploymentType.edgeManifest.toString()))
            {
                this.deploymentType = DeploymentType.edgeManifest;
            }
            else if (deployment.getLabels().containsValue(DeploymentType.deviceConfiguration.toString()))
            {
                this.deploymentType = DeploymentType.deviceConfiguration;
            }
        }
        else
        {
            if (deployment.getContent().getModulesContent() != null)
            {
                this.deploymentType = DeploymentType.edgeManifest;
            }
            else if (deployment.getContent().getDeviceContent() != null)
            {
                this.deploymentType = DeploymentType.deviceConfiguration;
            }
        }


        this.configType = deployment.getLabels().get(ConfigurationsHelper.CONFIG_TYPE_LABEL.toString());

        this.deploymentMetrics = new DeploymentMetrics(deployment.getSystemMetrics(), deployment.getMetrics());
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getPackageContent() { return this.packageContent; }

    public String getPackageName() {
        return this.packageName;
    }

    public String getCreatedDateTimeUtc() {
        return this.createdDateTimeUtc;
    }

    public DeviceGroup getDeviceGroup() { return this.deviceGroup; }

    public int getPriority() {
        return this.priority;
    }

    public DeploymentType getDeploymentType() {
        return this.deploymentType;
    }

    public String getConfigType() {
        return this.configType;
    }

    public DeploymentMetrics getDeploymentMetrics() {
        return this.deploymentMetrics;
    }

    private String formatDateTimeToUTC(String originalTime) {
        DateTime dateTime = DateTime.parse(originalTime);

        // TODO: Remove workaround for Java SDK bug not returning time in UTC.
        try {
            dateTime = dateTime.toDateTime(DateTimeZone.UTC);
        } catch(Exception ex) {
            // Swallow exception and use date as-is.
        }

        return dateTime.toString(DATE_FORMAT_STRING);
    }
}
