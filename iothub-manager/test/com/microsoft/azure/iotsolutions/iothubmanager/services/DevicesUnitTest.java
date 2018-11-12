// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services;

import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.external.IStorageAdapterClient;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.*;
import com.microsoft.azure.sdk.iot.deps.twin.DeviceCapabilities;
import com.microsoft.azure.sdk.iot.service.Device;
import com.microsoft.azure.sdk.iot.service.DeviceStatus;
import com.microsoft.azure.sdk.iot.service.RegistryManager;
import com.microsoft.azure.sdk.iot.service.devicetwin.*;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class DevicesUnitTest {
    private IDevices devices;

    @Mock
    private RegistryManager registry;

    @Mock
    private DeviceTwin deviceTwinClient;

    @Mock
    private DeviceMethod deviceMethodClient;

    private final String iotHubHostName = "testhub";

    @Mock
    private IStorageAdapterClient storageAdapterClient;

    @Mock
    private QueryCollection queryCollection;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private static final String moduleTwinJson =
                    "{\n" +
                    "  \"deviceId\": \"%s\",\n" +
                    "  \"moduleId\": \"%s\",\n" +
                    "  \"etag\": \"AAAAAAAAACg=\",\n" +
                    "  \"deviceEtag\": \"MzE1ODE4NjEy\",\n" +
                    "  \"status\": \"enabled\",\n" +
                    "  \"statusUpdateTime\": \"0001-01-01T00:00:00-06:00\",\n" +
                    "  \"connectionState\": \"Disconnected\",\n" +
                    "  \"lastActivityTime\": \"0001-01-01T00:00:00-06:00\",\n" +
                    "  \"cloudToDeviceMessageCount\": 0,\n" +
                    "  \"authenticationType\": \"sas\",\n" +
                    "  \"x509Thumbprint\": {\n" +
                    "    \"primaryThumbprint\": null,\n" +
                    "    \"secondaryThumbprint\": null\n" +
                    "  },\n" +
                    "  \"version\": 1,\n" +
                    "  \"properties\": {\n" +
                    "    \"desired\": {\n" +
                    "      \"schemaVersion\": \"1.0\",\n" +
                    "      \"runtime\": {\n" +
                    "        \"type\": \"docker\",\n" +
                    "        \"settings\": {\n" +
                    "          \"loggingOptions\": \"\",\n" +
                    "          \"dockvers\": \"v1.25\"\n" +
                    "        }\n" +
                    "      }\n" +
                    "    },\n" +
                    "    \"reported\": {\n" +
                    "      \"schemaVersion\": \"1.0\",\n" +
                    "      \"version\": {\n" +
                    "        \"version\": \"1.0.1\",\n" +
                    "        \"build\": \"15962126\",\n" +
                    "        \"commit\": \"6e5e86dcf0c9a3732fc72a64d9ec9b0fcb2d6fad\"\n" +
                    "      },\n" +
                    "      \"lastDesiredVersion\": 40,\n" +
                    "      \"lastDesiredStatus\": {\n" +
                    "        \"code\": 200\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";

    public DevicesUnitTest() {
        MockitoAnnotations.initMocks(this);
        this.devices = new Devices(registry, deviceTwinClient, deviceMethodClient, iotHubHostName,
                storageAdapterClient);
    }

    @Test
    @Parameters({"deviceId, moduleId, false",
                 ", moduleId, true",
                 "deviceId, , true"})
    public void getModuleTwinTest(String deviceId, String moduleId, boolean exceptionExpected) throws
            Exception {
        if (exceptionExpected) {
            exception.expect(InvalidInputException.class);
            this.devices.getModuleTwinAsync(deviceId, moduleId);
        } else {
            // Arrange
            final String query = String.format("SELECT \\* FROM devices.modules where deviceId = '%s' and " +
                    "moduleId = '%s'", deviceId, moduleId);
            final String pattern = "(?i)" + query;

            when(this.deviceTwinClient.queryTwinCollection(matches(pattern))).thenReturn(this.queryCollection);
            when(this.deviceTwinClient.hasNext(this.queryCollection)).thenReturn(true, false);

            final QueryCollectionResponse<DeviceTwinDevice> response = this.createQueryResponse(deviceId, moduleId);
            when(this.deviceTwinClient.next(eq(this.queryCollection), any())).thenReturn(response);

            // Act
            final TwinServiceModel module = this.devices.getModuleTwinAsync(deviceId, moduleId)
                    .toCompletableFuture().get();

            // Assert
            assertEquals(deviceId, module.getDeviceId());
            assertEquals(moduleId, module.getModuleId());
        }
    }

    @Test
    @Parameters({" , SELECT \\* FROM devices.modules",
                 "deviceId='test', SELECT \\* FROM devices.modules where deviceId='test'"})
    public void getModuleTwinTest(String query, String queryToMatch) throws
            Exception {
        // Arrange
        final String pattern = "(?i)" + query;

        when(this.deviceTwinClient.queryTwinCollection(matches(queryToMatch))).thenReturn(this.queryCollection);
        when(this.deviceTwinClient.hasNext(this.queryCollection)).thenReturn(true, false);
        final QueryCollectionResponse<DeviceTwinDevice> response = this.createQueryResponse("test", "test");
        when(this.deviceTwinClient.next(eq(this.queryCollection), any())).thenReturn(response);

        // Act
        final TwinServiceListModel model = this.devices.getModuleTwinsByQueryAsync(query, StringUtils.EMPTY)
                .toCompletableFuture().get();

        // Assert
        assertEquals(1, model.getItems().size());
        assertEquals("test", model.getItems().get(0).getDeviceId());
    }

    @Test
    @Parameters({"SelfSigned",
                 "CertificateAuthority"})
    public void invalidAuthenticationTypeForEdgeDeviceTest(String authTypeString) throws InvalidInputException, ExternalDependencyException {
        // Arrange
        final AuthenticationType authType = AuthenticationType.valueOf(authTypeString);

        AuthenticationMechanismServiceModel auth = new AuthenticationMechanismServiceModel();
        auth.setAuthenticationType(authType);

        DeviceServiceModel model = new DeviceServiceModel
            (
                "etag",
                "deviceId",
                0,
                DateTime.now(),
                true,
                true,
                true,
                DateTime.now(),
                null,
                auth,
                this.iotHubHostName
            );

        // Act & Assert
        exception.expect(InvalidInputException.class);
        this.devices.createAsync(model);
    }

    private QueryCollectionResponse<DeviceTwinDevice> createQueryResponse(String deviceId, String moduleId) {
        final QueryCollectionResponse<DeviceTwinDevice> resp = Mockito.mock(QueryCollectionResponse.class);

        final List<DeviceTwinDevice> respList = new ArrayList<>();
        respList.add(new DeviceTwinDevice(deviceId, moduleId));
        when(resp.getCollection()).thenReturn(respList);

        return resp;
    }

    private static Device createTestDevice(boolean isEdgeDevice)
    {
        Device dvc = Device.createFromId("deviceId", DeviceStatus.Enabled, null);
        DeviceCapabilities capabilities = new DeviceCapabilities();
        capabilities.setIotEdge(isEdgeDevice);
        dvc.setCapabilities(capabilities);
        return dvc;
    }
}
