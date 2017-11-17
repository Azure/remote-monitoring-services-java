// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.webservice.controllers;

import com.microsoft.azure.iotsolutions.uiconfig.services.IStorage;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.BaseException;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.ConditionApiModel;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.DeviceGroup;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.DeviceGroupCondition;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.assertEquals;

public class DeviceGroupControllerTest {

    private IStorage mockStorage;
    private DeviceGroupController controller;
    private Random rand;
    private final String condition = "[{\n" +
            "\"Operator\":\"LT\",\n" +
            "\"Value\":\"Value1\",\n" +
            "\"Key\":\"Key1\"\n" +
            "},{\n" +
            "\"Operator\":\"EQ\",\n" +
            "\"Value\":\"Value2\",\n" +
            "\"Key\":\"Key2\"\n" +
            "}]";

    @Before
    public void setUp() {
        mockStorage = Mockito.mock(IStorage.class);
        rand = new Random();
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void getAllAsyncTest() throws BaseException, ExecutionException, InterruptedException {
        List<DeviceGroup> models = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            DeviceGroup model = new DeviceGroup(rand.NextString(), rand.NextString(), null, rand.NextString());
            models.add(model);
        }
        Mockito.when(mockStorage.getAllDeviceGroupsAsync())
                .thenReturn(CompletableFuture.supplyAsync(() -> models));
        controller = new DeviceGroupController(mockStorage);
        String resultStr = TestUtils.getString(controller.getAllAsync().toCompletableFuture().get());
        DeviceGroupListApiModel result = Json.fromJson(Json.parse(resultStr), DeviceGroupListApiModel.class);
        assertEquals(result.getItems().spliterator().getExactSizeIfKnown(), models.size());
        for (DeviceGroupApiModel item : result.getItems()) {
            DeviceGroup model = models.stream()
                    .filter(m -> m.getId().equals(item.getId())).findFirst().get();
            assertEquals(model.getDisplayName(), item.getDisplayName());
            assertEquals(Json.stringify(Json.toJson(model.getConditions())), Json.stringify(Json.toJson(item.getConditions())));
            assertEquals(model.getETag(), item.getETag());
        }
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void getAsyncTest() throws BaseException, ExecutionException, InterruptedException {
        String groupId = rand.NextString();
        String displayName = rand.NextString();
        Iterable<DeviceGroupCondition> conditions = null;
        String etag = rand.NextString();
        DeviceGroup model = new DeviceGroup(groupId, displayName, conditions, etag);
        Mockito.when(mockStorage.getDeviceGroupAsync(Mockito.any(String.class)))
                .thenReturn(CompletableFuture.supplyAsync(() -> model));
        controller = new DeviceGroupController(mockStorage);
        String resultStr = TestUtils.getString(controller.getAsync(groupId).toCompletableFuture().get());
        DeviceGroupApiModel result = Json.fromJson(Json.parse(resultStr), DeviceGroupApiModel.class);
        assertEquals(result.getDisplayName(), displayName);
        assertEquals(Json.stringify(Json.toJson(result.getConditions())), Json.stringify(Json.toJson(conditions)));
        assertEquals(result.getETag(), etag);
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void creatAsyncTest() throws Exception {
        String groupId = rand.NextString();
        String displayName = rand.NextString();
        Iterable<DeviceGroupCondition> conditions = Json.fromJson(Json.parse(condition), new ArrayList<DeviceGroupCondition>().getClass());
        String etag = rand.NextString();
        DeviceGroup model = new DeviceGroup(groupId, displayName, conditions, etag);
        Mockito.when(mockStorage.createDeviceGroupAsync(Mockito.any(DeviceGroup.class)))
                .thenReturn(CompletableFuture.supplyAsync(() -> model));
        controller = new DeviceGroupController(mockStorage);
        TestUtils.setRequest(String.format("{\"DisplayName\":\"%s\",\"Conditions\":%s}", displayName, condition));
        String resultStr = TestUtils.getString(controller.createAsync().toCompletableFuture().get());
        DeviceGroupApiModel result = Json.fromJson(Json.parse(resultStr), DeviceGroupApiModel.class);
        assertEquals(result.getId(), groupId);
        assertEquals(result.getDisplayName(), displayName);
        assertEquals(Json.stringify(Json.toJson(result.getConditions())), Json.stringify(Json.toJson(conditions)));
        assertEquals(result.getETag(), etag);
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void updateAsyncTest() throws Exception {
        String groupId = rand.NextString();
        String displayName = rand.NextString();
        Iterable<DeviceGroupCondition> conditions = Json.fromJson(Json.parse(condition), new ArrayList<DeviceGroupCondition>().getClass());
        String etagOld = rand.NextString();
        String etagNew = rand.NextString();
        DeviceGroup model = new DeviceGroup(groupId, displayName, conditions, etagNew);
        Mockito.when(mockStorage.updateDeviceGroupAsync(Mockito.any(String.class), Mockito.any(DeviceGroup.class), Mockito.any(String.class)))
                .thenReturn(CompletableFuture.supplyAsync(() -> model));
        controller = new DeviceGroupController(mockStorage);
        TestUtils.setRequest(String.format("{\"DisplayName\":\"%s\",\"Conditions\":%s,\"ETag\":\"%s\"}", displayName, condition, etagOld));
        String resultStr = TestUtils.getString(controller.updateAsync(groupId).toCompletableFuture().get());
        DeviceGroupApiModel result = Json.fromJson(Json.parse(resultStr), DeviceGroupApiModel.class);
        assertEquals(result.getId(), groupId);
        assertEquals(result.getDisplayName(), displayName);
        assertEquals(Json.stringify(Json.toJson(result.getConditions())), Json.stringify(Json.toJson(conditions)));
        assertEquals(result.getETag(), etagNew);
    }
}
