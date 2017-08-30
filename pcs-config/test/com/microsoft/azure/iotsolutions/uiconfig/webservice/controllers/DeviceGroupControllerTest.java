// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.webservice.controllers;

import com.microsoft.azure.iotsolutions.uiconfig.services.IStorage;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.BaseException;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.DeviceGroupServiceModel;
import com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.controllers.DeviceGroupController;
import com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.models.DeviceGroupApiModel;
import com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.models.DeviceGroupListApiModel;
import helpers.Random;
import helpers.TestUtils;
import helpers.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;
import play.libs.Json;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.assertEquals;

public class DeviceGroupControllerTest {

    private IStorage mockStorage;
    private DeviceGroupController controller;
    private Random rand;

    @Before
    public void setUp() {
        mockStorage = Mockito.mock(IStorage.class);
        rand = new Random();
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void getAllAsyncTest() throws BaseException, ExecutionException, InterruptedException {
        List<DeviceGroupServiceModel> models = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            DeviceGroupServiceModel model = new DeviceGroupServiceModel(rand.NextString(), rand.NextString(), rand.NextString(), rand.NextString());
            models.add(model);
        }
        Mockito.when(mockStorage.getAllDeviceGroupsAsync())
                .thenReturn(CompletableFuture.supplyAsync(() -> models));
        controller = new DeviceGroupController(mockStorage);
        String resultStr = TestUtils.getString(controller.getAllAsync().toCompletableFuture().get());
        DeviceGroupListApiModel result = Json.fromJson(Json.parse(resultStr), DeviceGroupListApiModel.class);
        assertEquals(result.getItems().spliterator().getExactSizeIfKnown(), models.size());
        for (DeviceGroupApiModel item : result.getItems()) {
            DeviceGroupServiceModel model = models.stream()
                    .filter(m -> m.getId().equals(item.getId())).findFirst().get();
            assertEquals(model.getDisplayName(), item.getDisplayName());
            assertEquals(model.getConditions(), item.getConditions());
            assertEquals(model.getETag(), item.getETag());
        }
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void getAsyncTest() throws BaseException, ExecutionException, InterruptedException {
        String groupId = rand.NextString();
        String displayName = rand.NextString();
        String conditions = rand.NextString();
        String etag = rand.NextString();
        DeviceGroupServiceModel model = new DeviceGroupServiceModel(groupId, displayName, conditions, etag);
        Mockito.when(mockStorage.getDeviceGroupAsync(Mockito.any(String.class)))
                .thenReturn(CompletableFuture.supplyAsync(() -> model));
        controller = new DeviceGroupController(mockStorage);
        String resultStr = TestUtils.getString(controller.getAsync(groupId).toCompletableFuture().get());
        DeviceGroupApiModel result = Json.fromJson(Json.parse(resultStr), DeviceGroupApiModel.class);
        assertEquals(result.getDisplayName(), displayName);
        assertEquals(result.getConditions(), conditions);
        assertEquals(result.getETag(), etag);
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void creatAsyncTest() throws BaseException, ExecutionException, InterruptedException {
        String groupId = rand.NextString();
        String displayName = rand.NextString();
        String conditions = rand.NextString();
        String etag = rand.NextString();
        DeviceGroupServiceModel model = new DeviceGroupServiceModel(groupId, displayName, conditions, etag);
        Mockito.when(mockStorage.createDeviceGroupAsync(Mockito.any(DeviceGroupServiceModel.class)))
                .thenReturn(CompletableFuture.supplyAsync(() -> model));
        controller = new DeviceGroupController(mockStorage);
        TestUtils.setRequest(String.format("{\"DisplayName\":\"%s\",\"Conditions\":\"%s\"}", displayName, conditions));
        String resultStr = TestUtils.getString(controller.createAsync().toCompletableFuture().get());
        DeviceGroupApiModel result = Json.fromJson(Json.parse(resultStr), DeviceGroupApiModel.class);
        assertEquals(result.getId(), groupId);
        assertEquals(result.getDisplayName(), displayName);
        assertEquals(result.getConditions(), conditions);
        assertEquals(result.getETag(), etag);
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void updateAsyncTest() throws BaseException, ExecutionException, InterruptedException {
        String groupId = rand.NextString();
        String displayName = rand.NextString();
        String conditions = rand.NextString();
        String etagOld = rand.NextString();
        String etagNew = rand.NextString();
        DeviceGroupServiceModel model = new DeviceGroupServiceModel(groupId, displayName, conditions, etagNew);
        Mockito.when(mockStorage.updateDeviceGroupAsync(Mockito.any(String.class), Mockito.any(DeviceGroupServiceModel.class), Mockito.any(String.class)))
                .thenReturn(CompletableFuture.supplyAsync(() -> model));
        controller = new DeviceGroupController(mockStorage);
        TestUtils.setRequest(String.format("{\"DisplayName\":\"%s\",\"Conditions\":\"%s\",\"ETag\":\"%s\"}", displayName, conditions, etagOld));
        String resultStr = TestUtils.getString(controller.updateAsync(groupId).toCompletableFuture().get());
        DeviceGroupApiModel result = Json.fromJson(Json.parse(resultStr), DeviceGroupApiModel.class);
        assertEquals(result.getId(), groupId);
        assertEquals(result.getDisplayName(), displayName);
        assertEquals(result.getConditions(), conditions);
        assertEquals(result.getETag(), etagNew);
    }
}
