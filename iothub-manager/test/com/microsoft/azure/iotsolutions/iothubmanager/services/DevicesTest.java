// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services;

import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.*;
import com.microsoft.azure.iotsolutions.iothubmanager.services.external.ConfigService;
import com.microsoft.azure.iotsolutions.iothubmanager.services.external.IConfigService;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.*;
import com.microsoft.azure.iotsolutions.iothubmanager.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.runtime.Config;
import com.microsoft.azure.sdk.iot.device.*;
import com.microsoft.azure.sdk.iot.service.auth.SymmetricKey;
import helpers.IntegrationTest;
import helpers.DeviceMethodEmulator;
import org.junit.*;
import org.junit.experimental.categories.Category;
import play.test.WSTestClient;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.*;

public class DevicesTest {

    private static Config config;
    private static IServicesConfig servicesConfig;
    private static IConfigService configService;
    private static IIoTHubWrapper ioTHubWrapper;
    private static IDevices deviceService;
    private static ArrayList<DeviceServiceModel> testDevices = new ArrayList<>();
    private static ArrayList<DeviceClient> testDeviceEmulators = new ArrayList<>();
    private static String batchId = UUID.randomUUID().toString().replace("-", "");
    private static final String MALFORMED_JSON_EXCEED_5_LEVELS = "Malformed Json: exceed 5 levels";

    private static boolean setUpIsDone = false;

    @BeforeClass
    public static void setUpOnce() throws Exception {
        if (setUpIsDone) {
            return;
        }

        config = new Config();
        servicesConfig = config.getServicesConfig();
        configService = new ConfigService(servicesConfig, WSTestClient.newClient(9005));
        ioTHubWrapper = new IoTHubWrapper(servicesConfig);
        deviceService = new Devices(ioTHubWrapper, configService);

        createTestDevices(2, batchId);

        setUpIsDone = true;
    }

    @AfterClass
    public static void tearDownOnce() {
        for (DeviceClient client : testDeviceEmulators) {
            try {
                System.out.println("Shutting down device emulator...");
                client.closeNow();
            } catch (IOException e) {
                System.out.println("Warning: Error on shutting down device emulator");
            }
        }
        for (DeviceServiceModel device : testDevices) {
            try {
                deviceService.deleteAsync(device.getId()).toCompletableFuture().get();
                System.out.println(device.getId() + " deleted");
            } catch (Exception e) {
                System.out.println(String.format("Warning: Unable to destroy test devices: %s, error: %s",
                    device.getId(), e.getMessage()));
            }
        }
    }

    @Test(timeout = 100000)
    @Category({IntegrationTest.class})
    public void getAllAsyncTest() {
        try {
            DeviceServiceListModel deviceList = deviceService.queryAsync("", "").toCompletableFuture().get();
            Assert.assertTrue(deviceList.getItems().size() >= testDevices.size());
        } catch (Exception e) {
            ignoreMalformedJsonExceptions(e, "Unable to get all devices");
        }
    }

    @Test(timeout = 100000)
    @Category({IntegrationTest.class})
    public void queryByRawStringAsyncTest() {
        try {
            String rawQueryString = String.format("Tags.BatchId='%s'", batchId);
            DeviceServiceListModel deviceList = deviceService.queryAsync(rawQueryString, "").toCompletableFuture().get();
            Assert.assertTrue(deviceList.getItems().size() == testDevices.size());
        } catch (Exception e) {
            ignoreMalformedJsonExceptions(e, "Unable to query devices by raw query string");
        }
    }

    @Test(timeout = 100000)
    @Category({IntegrationTest.class})
    public void queryByJsonStringAsyncTest() {
        try {
            String jsonString = String.format("[{ \"Key\": \"Tags.BatchId\", \"Operator\": \"EQ\", \"Value\": \"%s\" }]", batchId);
            DeviceServiceListModel deviceList = deviceService.queryAsync(jsonString, "").toCompletableFuture().get();
            Assert.assertTrue(deviceList.getItems().size() > 0);
        } catch (Exception e) {
            ignoreMalformedJsonExceptions(e, "Unable to query devices by json query string");
        }
    }

    @Test(timeout = 10000)
    @Category({IntegrationTest.class})
    public void getAsyncFailureTest() throws Exception {
        try {
            deviceService.getAsync("IncorrectDeviceId").toCompletableFuture().get();
        } catch (Exception ex) {
            Assert.assertTrue(ex.getCause() instanceof ResourceNotFoundException);
        }
    }

    @Test(timeout = 100000)
    @Category({IntegrationTest.class})
    public void getAsyncSuccessTest() throws Exception {
        try {
            DeviceServiceListModel devices = deviceService.queryAsync("", "").toCompletableFuture().get();
            DeviceServiceModel device = deviceService.getAsync(devices.getItems().get(0).getId()).toCompletableFuture().get();
            Assert.assertEquals(device.getId(), devices.getItems().get(0).getId());
            Assert.assertNotNull(device.getTwin());
        } catch (Exception e) {
            ignoreMalformedJsonExceptions(e, "Unable to get all devices");
        }
    }

    @Test(timeout = 100000)
    @Category({IntegrationTest.class})
    public void createAndDeleteAsyncSuccessTest() throws Exception {
        String deviceId = randomDeviceId();
        String eTag = "etagxx==";
        HashMap<String, Object> tags = new HashMap<String, Object>() {{
            put("Building", "Building40");
            put("Floor", "1F");
        }};
        HashMap<String, Object> desired;
        desired = new HashMap() {
            {
                put("Config", new HashMap<String, Object>() {
                    {
                        put("Test", 1);
                    }
                });
            }
        };

        DeviceTwinProperties properties = new DeviceTwinProperties(desired, null);
        DeviceTwinServiceModel twin = new DeviceTwinServiceModel(eTag, deviceId, properties, tags, true);
        DeviceServiceModel device = new DeviceServiceModel(eTag, deviceId, 0, null, false, true, null, twin, null, null);
        DeviceServiceModel newDevice = deviceService.createAsync(device).toCompletableFuture().get();
        DeviceTwinServiceModel newTwin = newDevice.getTwin();
        HashMap<String, Object> configMap = (HashMap) newTwin.getProperties().getDesired().get("Config");

        Assert.assertEquals(deviceId, newDevice.getId());
        Assert.assertNull(newDevice.getAuthentication().getPrimaryThumbprint());
        Assert.assertNull(newDevice.getAuthentication().getSecondaryThumbprint());
        Assert.assertEquals(newTwin.getTags().get("Building"), "Building40");
        Assert.assertEquals(newTwin.getTags().get("Floor"), "1F");
        Assert.assertEquals(configMap.get("Test"), 1);

        Assert.assertTrue(deviceService.deleteAsync(deviceId).toCompletableFuture().get());
    }

    @Test(timeout = 100000)
    @Category({IntegrationTest.class})
    public void createDeviceWithSasTokenAsyncTest() throws Exception {
        String deviceId = randomDeviceId();
        String eTag = "etagxx==";
        HashMap<String, Object> tags = new HashMap<String, Object>() {{
            put("Building", "Building40");
            put("Floor", "1F");
        }};
        SymmetricKey key = new SymmetricKey();
        AuthenticationMechanismServiceModel authModel = new AuthenticationMechanismServiceModel(AuthenticationType.Sas);
        authModel.setPrimaryKey(key.getPrimaryKey());
        authModel.setSecondaryKey(key.getSecondaryKey());

        DeviceTwinServiceModel twin = new DeviceTwinServiceModel(eTag, deviceId, null, tags, true);
        DeviceServiceModel device = new DeviceServiceModel(eTag, deviceId, 0, null, false, true, null, twin, authModel, null);
        DeviceServiceModel newDevice = deviceService.createAsync(device).toCompletableFuture().get();

        Assert.assertNotNull(newDevice.getId());
        Assert.assertNull(newDevice.getAuthentication().getPrimaryThumbprint());
        Assert.assertNull(newDevice.getAuthentication().getSecondaryThumbprint());
        Assert.assertEquals(key.getPrimaryKey(), newDevice.getAuthentication().getPrimaryKey());
        Assert.assertEquals(key.getSecondaryKey(), newDevice.getAuthentication().getSecondaryKey());

        Assert.assertTrue(deviceService.deleteAsync(deviceId).toCompletableFuture().get());
    }

    @Test(timeout = 100000)
    @Category({IntegrationTest.class})
    public void createDeviceWithX509CertificationAsyncTest() throws Exception {
        String deviceId = randomDeviceId();
        String eTag = "etagxx==";
        HashMap<String, Object> tags = new HashMap<String, Object>() {{
            put("Building", "Building40");
            put("Floor", "1F");
        }};
        AuthenticationMechanismServiceModel authModel = new AuthenticationMechanismServiceModel(AuthenticationType.SelfSinged);

        DeviceTwinServiceModel twin = new DeviceTwinServiceModel(eTag, deviceId, null, tags, true);
        DeviceServiceModel device = new DeviceServiceModel(eTag, deviceId, 0, null, false, true, null, twin, authModel, null);
        DeviceServiceModel newDevice = deviceService.createAsync(device).toCompletableFuture().get();

        Assert.assertNotNull(newDevice.getId());
        Assert.assertNull(newDevice.getAuthentication().getPrimaryKey());
        Assert.assertNull(newDevice.getAuthentication().getSecondaryKey());
        Assert.assertEquals(authModel.getPrimaryThumbprint(), newDevice.getAuthentication().getPrimaryThumbprint());
        Assert.assertEquals(authModel.getSecondaryThumbprint(), newDevice.getAuthentication().getSecondaryThumbprint());
        Assert.assertTrue(deviceService.deleteAsync(deviceId).toCompletableFuture().get());
    }

    @Test(timeout = 100000)
    @Category({IntegrationTest.class})
    public void createWithoutIdAsyncSuccessTest() throws Exception {
        String deviceId = "";
        String eTag = "etagxx==";
        HashMap<String, Object> tags = new HashMap<String, Object>() {{
            put("Building", "Building40");
        }};
        DeviceTwinProperties properties = new DeviceTwinProperties(null, null);
        DeviceTwinServiceModel twin = new DeviceTwinServiceModel(eTag, deviceId, properties, tags, true);
        DeviceServiceModel device = new DeviceServiceModel(eTag, deviceId, 0, null, false, true, null, twin, null, null);
        DeviceServiceModel newDevice = deviceService.createAsync(device).toCompletableFuture().get();
        Assert.assertNotNull(newDevice.getId());
        Assert.assertTrue(deviceService.deleteAsync(newDevice.getId()).toCompletableFuture().get());
    }

    @Test(timeout = 100000)
    @Category({IntegrationTest.class})
    public void createOrUpdateTest() throws Exception {
        String deviceId = randomDeviceId();
        String eTag = "etagxx==";
        HashMap<String, Object> tags = new HashMap<String, Object>() {{
            put("Building", "Building40");
        }};
        HashMap desired = new HashMap() {
            {
                put("Config", new HashMap<String, Object>() {
                    {
                        put("Test", 1);
                    }
                });
            }
        };
        DeviceTwinProperties properties = new DeviceTwinProperties(desired, null);
        DeviceTwinServiceModel twin = new DeviceTwinServiceModel(eTag, deviceId, properties, tags, true);
        DeviceServiceModel device = new DeviceServiceModel(eTag, deviceId, 0, null, false, true, null, twin, null, null);
        DeviceServiceModel newDevice = deviceService.createOrUpdateAsync(device.getId(), device).toCompletableFuture().get();
        DeviceTwinServiceModel newTwin = newDevice.getTwin();
        HashMap<String, Object> configMap = (HashMap) newTwin.getProperties().getDesired().get("Config");
        Assert.assertEquals(deviceId, newDevice.getId());
        Assert.assertEquals(newTwin.getTags().get("Building"), "Building40");
        Assert.assertEquals(configMap.get("Test"), 1);
        Assert.assertTrue(deviceService.deleteAsync(deviceId).toCompletableFuture().get());
    }

    @Test(timeout = 100000, expected = InvalidInputException.class)
    @Category({IntegrationTest.class})
    public void createOrUpdateWithMismatchedIdFailureTest() throws Exception {
        String deviceId = randomDeviceId();
        String eTag = "etagxx==";
        DeviceTwinServiceModel twin = new DeviceTwinServiceModel(eTag, "MismatchedDeviceID", null, null, true);
        DeviceServiceModel device = new DeviceServiceModel(eTag, "MismatchedDeviceID", 0, null, false, true, null, twin, null, null);
        deviceService.createOrUpdateAsync(deviceId, device).toCompletableFuture().get();
    }

    @Test(timeout = 100000, expected = InvalidInputException.class)
    @Category({IntegrationTest.class})
    public void createOrUpdateWithEmptyIdFailureTest() throws Exception {
        String deviceId = randomDeviceId();
        String eTag = "etagxx==";
        DeviceTwinServiceModel twin = new DeviceTwinServiceModel(eTag, "", null, null, true);
        DeviceServiceModel device = new DeviceServiceModel(eTag, "", 0, null, false, true, null, twin, null, null);
        deviceService.createOrUpdateAsync(deviceId, device).toCompletableFuture().get();
    }

    @Test(timeout = 10000)
    @Category({IntegrationTest.class})
    public void deleteAsyncFailureTest() throws Exception {
        try {
            deviceService.deleteAsync("IncorrectDeviceId").toCompletableFuture().get();
        } catch (Exception ex) {
            Assert.assertTrue(ex.getCause() instanceof ResourceNotFoundException);
        }
    }

    @Test(timeout = 100000)
    @Category({IntegrationTest.class})
    public void invokeMethodAsyncTest() {
        try {
            DeviceServiceModel targetDevice = testDevices.get(0);
            MethodParameterServiceModel parameter = new MethodParameterServiceModel();
            parameter.setName("Reboot");
            parameter.setJsonPayload("");
            parameter.setResponseTimeout(Duration.ofSeconds(5));
            parameter.setConnectionTimeout(Duration.ofSeconds(5));
            MethodResultServiceModel result = deviceService.invokeDeviceMethodAsync(
                targetDevice.getId(), parameter).toCompletableFuture().get();
            Assert.assertTrue(result.getStatus() == 200);
            Assert.assertTrue(result.getJsonPayload().contains("Reboot accepted"));
        } catch (Exception e) {
            ignoreMalformedJsonExceptions(e, "Unable to invoke method");
        }
    }

    private void ignoreMalformedJsonExceptions(Exception e, String message) {
        // In order to make the test resilient to the issue 158 of Java SDK,
        // the expected exception will be checked if the issue is hit.
        // see more detail at https://github.com/Azure/azure-iot-sdk-java/issues/158
        if (e.getCause() instanceof IllegalArgumentException) {
            Assert.assertTrue(e.getMessage().contains(MALFORMED_JSON_EXCEED_5_LEVELS));
        } else {
            Assert.fail(message);
        }
    }

    private static void createTestDevices(int count, String batchId) {
        try {
            for (int i = 0; i < count; i++) {
                String deviceId = String.format("IntegrationTestDevice_%s_%s", batchId, i);
                String eTag = "etagxx==";
                HashMap<String, Object> tags = new HashMap<String, Object>() {{
                    put("BatchId", batchId);
                    put("Purpose", "IntegrationTest");
                }};
                HashMap desired = new HashMap() {
                    {
                        put("Config", new HashMap<String, Object>() {
                            {
                                put("Test", 1);
                            }
                        });
                    }
                };
                DeviceTwinProperties properties = new DeviceTwinProperties(desired, null);
                DeviceTwinServiceModel twin = new DeviceTwinServiceModel(eTag, deviceId, properties, tags, true);
                DeviceServiceModel device = new DeviceServiceModel(eTag, deviceId, 0, null, false, true, null, twin, null, null);
                DeviceServiceModel newDevice = deviceService.createAsync(device).toCompletableFuture().get();
                testDevices.add(newDevice);
                System.out.println(deviceId + " created");
                createDeviceEmulator(newDevice);
            }
        } catch (Exception e) {
            Assert.fail(String.format("Unable to create test devices cause by: %s", e.getMessage()));
        }
    }

    private static void createDeviceEmulator(DeviceServiceModel newDevice)
        throws IOException, URISyntaxException, InvalidConfigurationException {
        DeviceClient client = null;
        try {
            String connString = String.format("HostName=%s;DeviceId=%s;SharedAccessKey=%s",
                ioTHubWrapper.getIotHubHostName(),
                newDevice.getId(),
                newDevice.getAuthentication().getPrimaryKey());
            client = new DeviceClient(connString, IotHubClientProtocol.MQTT);
            client.open();
            System.out.println("Connected to IoT Hub.");
            client.subscribeToDeviceMethod(new DeviceMethodEmulator.DeviceMethodCallback(),
                null, new DeviceMethodEmulator.DeviceMethodStatusCallBack(), null);
            System.out.println("Subscribed to device method and waiting for method trigger");
            testDeviceEmulators.add(client);
        } catch (IOException e) {
            System.out.println(String.format("Warning: on exception: %s, shutting down device emulator",
                e.getMessage()));
            if (client != null) {
                client.closeNow();
            }
        }
    }

    private String randomDeviceId() {
        return "IntegrationTestDevice_" + UUID.randomUUID().toString().replace("-", "");
    }
}

