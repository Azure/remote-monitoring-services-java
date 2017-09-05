// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services;

import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.ResourceNotFoundException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.*;
import com.microsoft.azure.iotsolutions.iothubmanager.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.runtime.Config;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;
import helpers.UnitTest;
import org.junit.*;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

public class DevicesTest {

    private static Config config;
    private static IServicesConfig servicesConfig;
    private static IIoTHubWrapper ioTHubWrapper;
    private static IDevices deviceService;
    private static ArrayList<DeviceServiceModel> testDevices = new ArrayList<>();
    private static String batchId = UUID.randomUUID().toString().replace("-", "");

    private static boolean setUpIsDone = false;

    @BeforeClass
    public static void setUpOnce() throws Exception {
        if (setUpIsDone) {
            return;
        }

        config = new Config();
        servicesConfig = config.getServicesConfig();
        ioTHubWrapper = new IoTHubWrapper(servicesConfig);
        deviceService = new Devices(ioTHubWrapper);

        createTestDevices(2, batchId);

        setUpIsDone = true;
    }

    @AfterClass
    public static void tearDownOnce() {
        for (DeviceServiceModel device : testDevices) {
            try {
                deviceService.deleteAsync(device.getId()).toCompletableFuture().get();
                System.out.println(device.getId() + " deleted");
            } catch (Exception ex) {
                Assert.fail("Unable to destroy test deviceService");
            }
        }
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void getAllAsyncTest() throws IOException, IotHubException {
        try {
            DeviceServiceListModel deviceList = deviceService.queryAsync("", "").toCompletableFuture().get();
            Assert.assertTrue(deviceList.getItems().size() >= testDevices.size());
        } catch (Exception e) {
        }
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void queryByRawStringAsyncTest() throws IOException, IotHubException {
        try {
            String rawQueryString = String.format("Tags.BatchId='%s'", batchId);
            DeviceServiceListModel deviceList = deviceService.queryAsync(rawQueryString, "").toCompletableFuture().get();
            Assert.assertTrue(deviceList.getItems().size() == testDevices.size());
        } catch (Exception e) {
        }
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void queryByJsonStringAsyncTest() throws IOException, IotHubException {
        try {
            String jsonString = String.format("[\"Key\": \"Tags.BatchId\", \"Operator\": \"EQ\", \"Value\": \"%s\" ]", batchId);
            DeviceServiceListModel deviceList = deviceService.queryAsync(jsonString, "").toCompletableFuture().get();
            Assert.assertTrue(deviceList.getItems().size() > 0);
        } catch (Exception e) {
        }
    }

    @Test(timeout = 10000)
    @Category({UnitTest.class})
    public void getAsyncFailureTest() throws Exception {
        try {
            deviceService.getAsync("IncorrectDeviceId").toCompletableFuture().get();
        } catch (Exception ex) {
            Assert.assertTrue(ex.getCause() instanceof ResourceNotFoundException);
        }
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void getAsyncSuccessTest() throws Exception {
        DeviceServiceListModel devices = deviceService.queryAsync("", "").toCompletableFuture().get();
        DeviceServiceModel device = deviceService.getAsync(devices.getItems().get(0).getId()).toCompletableFuture().get();
        Assert.assertEquals(device.getId(), devices.getItems().get(0).getId());
        Assert.assertNotNull(device.getTwin());
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void createAndDeleteAsyncSuccessTest() throws Exception {
        String deviceId = "unitTestDevice_" + UUID.randomUUID().toString().replace("-", "");
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
        Assert.assertEquals(newTwin.getTags().get("Building"), "Building40");
        Assert.assertEquals(newTwin.getTags().get("Floor"), "1F");
        Assert.assertEquals(configMap.get("Test"), 1);
        Assert.assertTrue(deviceService.deleteAsync(deviceId).toCompletableFuture().get());
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void createOrUpdateTest() throws Exception {
        String deviceId = "unitTestDevice_" + UUID.randomUUID().toString().replace("-", "");
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

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void createOrUpdateWithMismatchedIdFailureTest() {
        try {
            String deviceId = "unitTestDevice_" + UUID.randomUUID().toString().replace("-", "");
            String eTag = "etagxx==";
            DeviceTwinServiceModel twin = new DeviceTwinServiceModel(eTag, "MismatchedDeviceID", null, null, true);
            DeviceServiceModel device = new DeviceServiceModel(eTag, "MismatchedDeviceID", 0, null, false, true, null, twin, null, null);
            DeviceServiceModel newDevice = deviceService.createOrUpdateAsync(deviceId, device).toCompletableFuture().get();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof InvalidInputException);
        }
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void createOrUpdateWithEmptyIdFailureTest() {
        try {
            String deviceId = "unitTestDevice_" + UUID.randomUUID().toString().replace("-", "");
            String eTag = "etagxx==";
            DeviceTwinServiceModel twin = new DeviceTwinServiceModel(eTag, "", null, null, true);
            DeviceServiceModel device = new DeviceServiceModel(eTag, "", 0, null, false, true, null, twin, null, null);
            DeviceServiceModel newDevice = deviceService.createOrUpdateAsync(deviceId, device).toCompletableFuture().get();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof InvalidInputException);
        }
    }

    @Test(timeout = 10000)
    @Category({UnitTest.class})
    public void deleteAsyncFailureTest() throws Exception {
        try {
            deviceService.deleteAsync("IncorrectDeviceId").toCompletableFuture().get();
        } catch (Exception ex) {
            Assert.assertTrue(ex.getCause() instanceof ResourceNotFoundException);
        }
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void invokeMethodAsyncTest() {
        try {
            DeviceServiceListModel deviceList = deviceService.queryAsync("", "").toCompletableFuture().get();
            DeviceServiceModel targetDevice = null;
            for (DeviceServiceModel device : deviceList.getItems()) {
                if (device.getTwin().getProperties().getReported().containsKey("SupportedMethods")) {
                    targetDevice = device;
                    break;
                }
            }

            if(targetDevice != null) {
                MethodParameterServiceModel parameter = new MethodParameterServiceModel();
                parameter.setName("Reboot");
                parameter.setJsonPayload("");
                parameter.setResponseTimeout(Duration.ofSeconds(5));
                parameter.setConnectionTimeout(Duration.ofSeconds(5));
                MethodResultServiceModel result = deviceService.invokeDeviceMethodAsync(
                    targetDevice.getId(), parameter).toCompletableFuture().get();
                Assert.assertTrue(result.getStatus() == 200);
                Assert.assertTrue(result.getJsonPayload().contains("Reboot accepted"));
            }
        } catch (Exception e) {
            Assert.fail("Unable to invoke method");
        }
    }

    private static void createTestDevices(int count, String batchId) {
        try {
            for (int i = 0; i < count; i++) {
                String deviceId = String.format("unitTestDevice-%s_%s", i, batchId);
                String eTag = "etagxx==";
                HashMap<String, Object> tags = new HashMap<String, Object>() {{
                    put("BatchId", batchId);
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
                System.out.println(deviceId + "created");
            }
        } catch (Exception e) {
            Assert.fail("Unable to create test deviceService");
        }
    }
}

