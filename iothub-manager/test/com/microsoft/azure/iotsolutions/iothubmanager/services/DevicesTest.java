// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services;

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
import java.util.*;

public class DevicesTest {

    private Config config;
    private IServicesConfig servicesConfig;
    private IIoTHubWrapper ioTHubWrapper;
    private IDevices devices;

    @Before
    public void setUp() throws Exception {
        this.config = new Config();
        this.servicesConfig = config.getServicesConfig();
        this.ioTHubWrapper = new IoTHubWrapper(servicesConfig);
        this.devices = new Devices(ioTHubWrapper);
    }

    @After
    public void tearDown() {
        // something after every test
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test(timeout = 10000)
    @Category({UnitTest.class})
    public void getListAsyncTest() throws IOException, IotHubException {
        try {
            ArrayList<DeviceServiceModel> devices = this.devices.getListAsync().toCompletableFuture().get();
            Assert.assertNotNull(devices);
        } catch (Exception e) {
        }
    }

    @Test(timeout = 10000)
    @Category({UnitTest.class})
    public void getAsyncFailureTest() throws Exception {
        try {
            this.devices.getAsync("IncorrectDeviceId").toCompletableFuture().get();
        } catch (Exception ex) {
            Assert.assertTrue(ex.getCause() instanceof ResourceNotFoundException);
        }
    }

    @Test(timeout = 10000)
    @Category({UnitTest.class})
    public void getAsyncSuccessTest() throws Exception {
        ArrayList<DeviceServiceModel> devices = this.devices.getListAsync().toCompletableFuture().get();
        if (devices.size() > 0) {
            DeviceServiceModel device = this.devices.getAsync(devices.get(0).getId()).toCompletableFuture().get();
            Assert.assertEquals(device.getId(), devices.get(0).getId());
            Assert.assertNotNull(device.getTwin());
        }
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
        HashMap<String, Object> desired = new HashMap() {
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
        DeviceServiceModel newDevice = this.devices.createAsync(device).toCompletableFuture().get();
        Assert.assertEquals(deviceId, newDevice.getId());
        DeviceTwinServiceModel newTwin = newDevice.getTwin();
        Assert.assertEquals(newTwin.getTags().get("Building"), "Building40");
        Assert.assertEquals(newTwin.getTags().get("Floor"), "1F");
        HashMap<String, Object> configHash = (HashMap) newTwin.getProperties().getDesired().get("Config");
        Assert.assertEquals(configHash.get("Test"), 1);
        Assert.assertTrue(this.devices.deleteAsync(deviceId).toCompletableFuture().get());
    }

    @Test(timeout = 10000)
    @Category({UnitTest.class})
    public void deleteAsyncFailureTest() throws Exception {
        try {
            this.devices.deleteAsync("IncorrectDeviceId").toCompletableFuture().get();
        } catch (Exception ex) {
            Assert.assertTrue(ex.getCause() instanceof ResourceNotFoundException);
        }
    }
}

