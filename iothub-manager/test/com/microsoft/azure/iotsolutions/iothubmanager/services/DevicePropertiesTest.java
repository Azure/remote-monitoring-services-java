// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services;

import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.BaseException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.ResourceNotFoundException;
import com.microsoft.azure.iotsolutions.iothubmanager.services.external.IStorageAdapterClient;
import com.microsoft.azure.iotsolutions.iothubmanager.services.external.ValueApiModel;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DevicePropertyServiceModel;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceTwinName;
import com.microsoft.azure.iotsolutions.iothubmanager.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.iothubmanager.services.runtime.ServicesConfig;
import helpers.UnitTest;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;
import play.libs.Json;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class DevicePropertiesTest {
    private IStorageAdapterClient mockStorageAdapterClient;
    private IDevices mockDevices;
    private String cacheModel = "";
    private IServicesConfig config;
    private DeviceProperties deviceProperties;
    private HashMap<String, String> metadata = null;

    @Before
    public void setUp() {
        metadata = new HashMap<>();
        metadata.put("$modified", DateTime.now().plusDays(2).toString("yyyy-MM-dd'T'HH:mm:ssZZ"));
        mockStorageAdapterClient = Mockito.mock(IStorageAdapterClient.class);
        mockDevices = Mockito.mock(IDevices.class);
        cacheModel = "{\"Rebuilding\": false,\"Tags\": [ \"c\", \"a\", \"y\", \"z\" ],\"Reported\": [\"1\",\"9\",\"2\",\"3\"] }";
        List<String> cacheWhiteList = Arrays.asList("tags.*", "reported.*");
        config = new ServicesConfig(null, null, 0, 0, cacheWhiteList);
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void GetListAsyncTestAsync() throws BaseException, ExecutionException, InterruptedException {
        Mockito.when(mockStorageAdapterClient.getAsync(Mockito.any(String.class), Mockito.any(String.class)))
            .thenReturn(CompletableFuture.supplyAsync(() -> new ValueApiModel("", this.cacheModel, "", metadata)));
        deviceProperties = new DeviceProperties(mockStorageAdapterClient, config, mockDevices);
        TreeSet<String> result = this.deviceProperties.getListAsync().toCompletableFuture().get();
        assertEquals(String.join(",", result), "Tags.z,Tags.y,Tags.c,Tags.a,Properties.Reported.9,Properties.Reported.3,Properties.Reported.2,Properties.Reported.1");
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void UpdateListAsyncTestAsync() throws BaseException, ExecutionException, InterruptedException {
        Mockito.when(mockStorageAdapterClient.getAsync(Mockito.any(String.class), Mockito.any(String.class)))
            .thenReturn(CompletableFuture.supplyAsync(() -> new ValueApiModel("", this.cacheModel, "", metadata)));
        DevicePropertyServiceModel resultModel = new DevicePropertyServiceModel(new HashSet<String>(Arrays.asList("c", "a", "y", "z", "@", "#")),
            new HashSet<String>(Arrays.asList("1", "9", "2", "3", "12", "11")), false);
        DevicePropertyServiceModel model = new DevicePropertyServiceModel(new HashSet<String>(Arrays.asList("a", "y", "z", "@", "#")),
            new HashSet<String>(Arrays.asList("9", "2", "3", "11", "12")), false);
        Mockito.when(mockStorageAdapterClient.updateAsync(Mockito.any(String.class), Mockito.any(String.class),
            Mockito.any(String.class), Mockito.any(String.class)))
            .thenReturn(CompletableFuture.supplyAsync(() -> new ValueApiModel("", Json.stringify(Json.toJson(resultModel)), "", null)));
        deviceProperties = new DeviceProperties(mockStorageAdapterClient, config, mockDevices);
        DevicePropertyServiceModel result = this.deviceProperties.updateListAsync(model).toCompletableFuture().get();
        assertEquals(String.join(",", new TreeSet<String>(result.getTags())), "#,@,a,c,y,z");
        assertEquals(String.join(",", new TreeSet<String>(result.getReported())), "1,11,12,2,3,9");
    }

    @Test(timeout = 300000)
    @Category({UnitTest.class})
    public void TryRecreateListAsyncSuccessTestAsync() throws Exception {
        Mockito.when(mockDevices.getDeviceTwinNames())
            .thenReturn(new DeviceTwinName(new HashSet<>(), new HashSet<>()))
            .thenReturn(new DeviceTwinName(new HashSet<>(Arrays.asList("tags.FieldService")), new HashSet<>()));

        Mockito.when(mockStorageAdapterClient.getAsync(Mockito.any(String.class), Mockito.any(String.class)))
            .thenThrow(new ResourceNotFoundException("The deviceProperties is not found"))
            .thenReturn(CompletableFuture.supplyAsync(() -> new ValueApiModel("", this.cacheModel, "", metadata)));
        DevicePropertyServiceModel emptyDevicePropertyServiceModel = new DevicePropertyServiceModel(new HashSet<>(), new HashSet<>(), false);
        DevicePropertyServiceModel noneEmptyDevicePropertyServiceModel = new DevicePropertyServiceModel(new HashSet<>(Arrays.asList("tags.FieldService")), new HashSet<>(Arrays.asList("reported.SupportedMethods")), false);
        Mockito.when(mockStorageAdapterClient.updateAsync(Mockito.any(String.class), Mockito.any(String.class),
            Mockito.any(String.class), Mockito.any()))
            .thenReturn(CompletableFuture.supplyAsync(() -> new ValueApiModel("", Json.stringify(Json.toJson(emptyDevicePropertyServiceModel)), "", null)))
            .thenReturn(CompletableFuture.supplyAsync(() -> new ValueApiModel("", Json.stringify(Json.toJson(noneEmptyDevicePropertyServiceModel)), "", null)));

        deviceProperties = new DeviceProperties(mockStorageAdapterClient, config, mockDevices);
        Boolean result = (Boolean) this.deviceProperties.tryRecreateListAsync(false).toCompletableFuture().get();
        assertTrue(result);
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void TryRecreateListAsyncFailureTestAsync() throws Exception {
        Mockito.when(mockDevices.getDeviceTwinNames())
            .thenReturn(new DeviceTwinName(new HashSet<>(Arrays.asList("tags.FieldService")), new HashSet<>()));
        String rebuildingCacheModel = "{\"Rebuilding\": true,\"Tags\": null,\"Reported\": null }";
        Mockito.when(mockStorageAdapterClient.getAsync(Mockito.any(String.class), Mockito.any(String.class)))
            .thenReturn(CompletableFuture.supplyAsync(() -> new ValueApiModel("", rebuildingCacheModel, "", metadata)));
        deviceProperties = new DeviceProperties(mockStorageAdapterClient, config, mockDevices);
        Boolean result = (Boolean) this.deviceProperties.tryRecreateListAsync(false).toCompletableFuture().get();
        assertFalse(result);
    }
}
