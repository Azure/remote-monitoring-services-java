// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services;

import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.*;
import com.microsoft.azure.sdk.iot.service.Configuration;
import com.microsoft.azure.sdk.iot.service.ConfigurationContent;
import com.microsoft.azure.sdk.iot.service.RegistryManager;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceTwin;
import com.microsoft.azure.sdk.iot.service.devicetwin.Query;
import com.microsoft.azure.sdk.iot.service.devicetwin.QueryType;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import play.libs.Json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class DeploymentsTest {
    private final Deployments deployments;
    private static final String DEPLOYMENT_NAME_LABEL = "Name";
    private static final String PACKAGE_TYPE_LABEL = "Type";
    private static final String CONFIG_TYPE_LABEL = "ConfigType";
    private static final String DEPLOYMENT_GROUP_ID_LABEL = "DeviceGroupId";
    private static final String DEPLOYMENT_GROUP_NAME_LABEL = "DeviceGroupName";
    private static final String DEPLOYMENT_PACKAGE_NAME_LABEL = "PackageName";
    private static final String RM_CREATED_LABEL = "RMDeployment";
    private static final String CONFIG_TYPE=StringUtils.EMPTY;

    @Mock
    private RegistryManager registry;

    @Mock
    private DeviceTwin deviceTwin;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    public DeploymentsTest() {
        MockitoAnnotations.initMocks(this);
        this.deployments = new Deployments("hubname",
                                           this.deviceTwin,
                                           this.registry);
    }

    @Test
    @Parameters({"depname, dvcgroupid, dvcGroupQuery, true, 10, false",
                 ", dvcgroupid, dvcGroupQuery, true, 10, true",
                 "depname, , dvcGroupQuery, true, 10, true",
                 "depname, dvcgroupid, , true, 10, true",
                 "depname, dvcgroupid, dvcGroupQuery, false, 10, true",
                 "depname, dvcgroupid, dvcGroupQuery, true, -1, true"})
    public void createDeploymentTest(String deploymentName, String deviceGroupId,
                                    String dvcGroupQuery, boolean addPackageContent, int priority,
                                    boolean exceptionExpected) throws Exception {
        // Arrange
        // Provides a different value to ensure that the configuration object returned
        // from creating the deployment is different than the one provided to the registry manager
        final String registryManagerDeploymentId = "test-config";

        final Configuration config = new Configuration(registryManagerDeploymentId);
        config.setLabels(new HashMap<>());
        config.getLabels().put(PACKAGE_TYPE_LABEL, PackageType.edgeManifest.toString());
        config.getLabels().put(CONFIG_TYPE_LABEL, CONFIG_TYPE);
        config.getLabels().put(DEPLOYMENT_NAME_LABEL, deploymentName);
        config.getLabels().put(DEPLOYMENT_GROUP_ID_LABEL, deviceGroupId);
        config.getLabels().put(RM_CREATED_LABEL, "true");
        config.setPriority(priority);
        final String packageContent = addPackageContent ? Json.toJson(config).toString() : StringUtils.EMPTY;

        final DeploymentServiceModel model = new DeploymentServiceModel(deploymentName,
                new DeviceGroup(deviceGroupId, StringUtils.EMPTY, dvcGroupQuery),
                packageContent,
                StringUtils.EMPTY,
                priority,
                PackageType.edgeManifest,
                CONFIG_TYPE);

        final IsValidConfiguration isValidConfig = new IsValidConfiguration(deploymentName, deviceGroupId);
        when(this.registry.addConfiguration(argThat(isValidConfig))).thenReturn(config);

        // Act & Assert
        if (exceptionExpected) {
            exception.expect(InvalidInputException.class);
            this.deployments.createAsync(model).toCompletableFuture().get();
        } else {
            DeploymentServiceModel createdDeployment = this.deployments.createAsync(model).toCompletableFuture().get();
            assertEquals(registryManagerDeploymentId, createdDeployment.getId());
            assertEquals(deploymentName, createdDeployment.getName());
            assertEquals(deviceGroupId, createdDeployment.getDeviceGroup().getId());
            assertEquals(priority, createdDeployment.getPriority());
        }
    }

    @Test
    @Parameters({"0","1","5"})
    public void getDeploymentsTest(int numDeployments) throws Exception {
        // Arrange
        List<Configuration> configurations = new ArrayList<>();
        for (int i = numDeployments - 1; i >= 0; i--) {
            configurations.add(this.createConfiguration(i, true));
        }

        when(this.registry.getConfigurations(20)).thenReturn(configurations);

        // Act
        DeploymentServiceListModel returnedDeployments = this.deployments.listAsync().toCompletableFuture()
                .get();
        assertEquals(numDeployments, returnedDeployments.getItems().size());

        // Assert - verify deployments are ordered by name
        for (int i = 0; i < numDeployments; i++)
        {
            final DeploymentServiceModel deployment = returnedDeployments.getItems().get(i);
            assertEquals("deployment" + i, deployment.getName());
            assertEquals("dvcGroupId" + i, deployment.getDeviceGroup().getId());
            assertEquals("dvcGroupName" + i, deployment.getDeviceGroup().getName());
            assertEquals("packageName" + i, deployment.getPackageName());
        }
    }

    @Test
    @Parameters({"true, true, true",
    "false, true, false",
    "true, true, false",
    "false, true, true",
    "true, false, false",
    "true, false, true"})
    public void getDeploymentTypeTest(boolean isEdgeContent, boolean addLabel, boolean isEdgeLabel) throws
            Exception
    {
        // Arrange
        ConfigurationContent deploymentContent = new ConfigurationContent()
        {
            {
                modulesContent = isEdgeContent ? new HashMap<String, Map<String, Object>>() : null;
                deviceContent = !(isEdgeContent) ? new HashMap<String, Object>() : null;
            }
        };

        String label = StringUtils.EMPTY;

        if (addLabel)
        {
            label = isEdgeLabel ? PackageType.edgeManifest.toString() : PackageType.deviceConfiguration.toString();
        }

        HashMap<String, String> deploymentLabels = new HashMap<String, String>();

        deploymentLabels.put(DEPLOYMENT_NAME_LABEL, StringUtils.EMPTY);
        deploymentLabels.put(DEPLOYMENT_GROUP_ID_LABEL, StringUtils.EMPTY);
        deploymentLabels.put(PACKAGE_TYPE_LABEL, label);
        deploymentLabels.put(CONFIG_TYPE_LABEL, "CustomConfig");
        deploymentLabels.put(RM_CREATED_LABEL, Boolean.TRUE.toString());

        Configuration configuration = new Configuration("test-config")
        {
            {
                labels = deploymentLabels;
                content = deploymentContent;
                priority = 10;
            }
        };

        String deploymentId = configuration.getId();
        when(this.registry.getConfiguration(deploymentId)).thenReturn(configuration);
        when(deviceTwin.queryTwin(anyString())).thenReturn(new Query(3, QueryType.TWIN));

        // Act
        DeploymentServiceModel returnedDeployment = this.deployments.getAsync(deploymentId, false)
                                                        .toCompletableFuture().get();

        // Assert Should returned Deployment Type according to label
        if (addLabel)
        {
            if (isEdgeLabel)
            {
                assertEquals(PackageType.edgeManifest, returnedDeployment.getPackageType());
            }
            else
            {
                assertEquals(PackageType.deviceConfiguration, returnedDeployment.getPackageType());
            }
        }
        else
        {
            if (isEdgeContent)
            {
                assertEquals(PackageType.edgeManifest, returnedDeployment.getPackageType());
            }
            else
            {
                assertEquals(PackageType.deviceConfiguration, returnedDeployment.getPackageType());
            }
        }
    }

    @Test
    @Parameters({
        "true", "false"
    })
    public void getDeploymentMetricsTest(boolean isEdgeDeployment) throws Exception
    {
        // Arrange
        ConfigurationContent depContent = new ConfigurationContent()
        {
            {
                modulesContent = isEdgeDeployment ? new HashMap<String, Map<String, Object>>() : null;
                deviceContent = !(isEdgeDeployment) ? new HashMap<String, Object>() : null;
            }
        };

        String label = isEdgeDeployment ? PackageType.edgeManifest.toString() : PackageType.deviceConfiguration.toString();

        String firmwareUpdateMxChip = "FirmwareUpdateMxChip";

        HashMap<String, String> depLabels = new HashMap<String, String>();

        depLabels.put(DEPLOYMENT_NAME_LABEL, StringUtils.EMPTY);
        depLabels.put(DEPLOYMENT_GROUP_ID_LABEL, StringUtils.EMPTY);
        depLabels.put(PACKAGE_TYPE_LABEL, label);
        depLabels.put(CONFIG_TYPE_LABEL, firmwareUpdateMxChip );
        depLabels.put(RM_CREATED_LABEL, Boolean.TRUE.toString());

        Configuration configuration = new Configuration("test-config")
        {
            {
                labels = depLabels;
                content = depContent;
                priority = 10;
            }
        };

        String deploymentId = configuration.getId();

        when(this.registry.getConfiguration(deploymentId)).thenReturn(configuration);
        when(deviceTwin.queryTwin(anyString())).thenReturn(new Query(3, QueryType.TWIN));

        // Act
        DeploymentServiceModel returnedDeployment = this.deployments.getAsync(deploymentId, false)
                                                        .toCompletableFuture().get();;

        // Assert Should returned Deployment Type according to label
        if (isEdgeDeployment)
        {
            Assert.assertNull(returnedDeployment.getDeploymentMetrics().getDeviceMetrics());
        }
        else
        {
            Assert.assertNotNull(returnedDeployment.getDeploymentMetrics().getDeviceMetrics());
        }
    }

    @Test
    public void filterOutNonRmDeploymentsTest() throws Exception {
        // Arrange
        final List<Configuration> configurations = new ArrayList<>();
        configurations.add(this.createConfiguration(0, true));
        configurations.add(this.createConfiguration(1, false));

        when(this.registry.getConfigurations(20)).thenReturn(configurations);

        // Act
        DeploymentServiceListModel returnedDeployments = this.deployments.listAsync().toCompletableFuture()
                .get();

        // Assert
        assertEquals(1, returnedDeployments.getItems().size());
        final DeploymentServiceModel deployment = returnedDeployments.getItems().get(0);
        assertEquals("deployment0", returnedDeployments.getItems().get(0).getName());
        assertEquals("dvcGroupId0", deployment.getDeviceGroup().getId());
        assertEquals("dvcGroupName0", deployment.getDeviceGroup().getName());
        assertEquals("packageName0", deployment.getPackageName());
    }

    private Configuration createConfiguration(int idx, boolean addCreatedByRmLabel)
    {
        final Configuration conf = new Configuration("test-config"+idx);
        final HashMap<String, String> labels = new HashMap<String, String>() {
            {
                put(PACKAGE_TYPE_LABEL, PackageType.edgeManifest.toString());
                put(CONFIG_TYPE_LABEL, CONFIG_TYPE);
                put(DEPLOYMENT_NAME_LABEL, "deployment" + idx);
                put(DEPLOYMENT_GROUP_ID_LABEL, "dvcGroupId" + idx);
                put(DEPLOYMENT_GROUP_NAME_LABEL, "dvcGroupName" + idx);
                put(DEPLOYMENT_PACKAGE_NAME_LABEL, "packageName" + idx);
            }
        };
        conf.setLabels(labels);
        conf.setPriority(10);

        if (addCreatedByRmLabel) {
            labels.put(RM_CREATED_LABEL, "true");
        }

        return conf;
    }

    class IsValidConfiguration implements ArgumentMatcher<Configuration> {

        private final String deploymentName;
        private final String deviceGroupId;

        IsValidConfiguration(final String deploymentName,
                                    final String deviceGroupId) {
            this.deploymentName = deploymentName;
            this.deviceGroupId = deviceGroupId;
        }

        @Override
        public boolean matches(Configuration config) {
            final Map<String, String> labels = config.getLabels();

            return labels.getOrDefault( DEPLOYMENT_NAME_LABEL,"").equals(deploymentName) &&
                    labels.getOrDefault( DEPLOYMENT_GROUP_ID_LABEL,"").equals(deviceGroupId) &&
                    labels.getOrDefault( RM_CREATED_LABEL,"").equals("true");
        }
    }
}
