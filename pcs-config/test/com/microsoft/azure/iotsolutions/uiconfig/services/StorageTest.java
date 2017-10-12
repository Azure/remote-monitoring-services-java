// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.BaseException;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.ConditionApiModel;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.IStorageAdapterClient;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.ValueApiModel;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.ValueListApiModel;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.DeviceGroup;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.DeviceGroupCondition;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.Logo;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.Theme;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.ServicesConfig;
import helpers.Random;
import helpers.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;
import play.libs.Json;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class StorageTest {

    private IStorageAdapterClient mockClient;
    private Storage storage;
    private Random rand;
    private ServicesConfig config;
    private String bingMapKey;

    @Before
    public void setUp() throws URISyntaxException, IOException {
        mockClient = Mockito.mock(IStorageAdapterClient.class);
        rand = new Random();
        config = new ServicesConfig();
        bingMapKey = rand.NextString();
        config.setBingMapKey(bingMapKey);
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void getThemeAsyncTest() throws BaseException, ExecutionException, InterruptedException {
        String name = rand.NextString();
        String description = rand.NextString();
        ValueApiModel model = new ValueApiModel();
        model.setData(String.format("{\"Name\":\"%s\",\"Description\":\"%s\"}", name, description));
        Mockito.when(mockClient.getAsync(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(CompletableFuture.supplyAsync(() -> model));
        storage = new Storage(mockClient, config);
        Object result = storage.getThemeAsync().toCompletableFuture().get();
        JsonNode node = Json.toJson(result);
        assertEquals(node.get("Name").asText(), name);
        assertEquals(node.get("Description").asText(), description);
        assertEquals(node.get("BingMapKey").asText(), bingMapKey);
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void getThemeAsyncDefaultTest() throws BaseException, ExecutionException, InterruptedException {
        Mockito.when(mockClient.getAsync(Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new BaseException());
        storage = new Storage(mockClient, config);
        Object result = storage.getThemeAsync().toCompletableFuture().get();
        JsonNode node = Json.toJson(result);
        assertEquals(node.get("Name").asText(), Theme.Default.getName());
        assertEquals(node.get("Description").asText(), Theme.Default.getDescription());
        assertEquals(node.get("BingMapKey").asText(), bingMapKey);
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void setThemeAsyncTest() throws BaseException, ExecutionException, InterruptedException {
        String name = rand.NextString();
        String description = rand.NextString();
        String jsonData = String.format("{\"Name\":\"%s\",\"Description\":\"%s\"}", name, description);
        Object theme = Json.fromJson(Json.parse(jsonData), Object.class);
        ValueApiModel model = new ValueApiModel();
        model.setData(jsonData);
        Mockito.when(mockClient.updateAsync(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).
                thenReturn(CompletableFuture.supplyAsync(() -> model));
        storage = new Storage(mockClient, config);
        Object result = storage.setThemeAsync(theme).toCompletableFuture().get();
        JsonNode node = Json.toJson(result);
        assertEquals(node.get("Name").asText(), name);
        assertEquals(node.get("Description").asText(), description);
        assertEquals(node.get("BingMapKey").asText(), bingMapKey);
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void getUserSettingAsyncTest() throws BaseException, ExecutionException, InterruptedException {
        String id = this.rand.NextString();
        String name = rand.NextString();
        String description = rand.NextString();
        String jsonData = String.format("{\"Name\":\"%s\",\"Description\":\"%s\"}", name, description);
        Object data = Json.fromJson(Json.parse(jsonData), Object.class);
        ValueApiModel model = new ValueApiModel();
        model.setData(jsonData);
        Mockito.when(mockClient.getAsync(Mockito.any(String.class), Mockito.any(String.class)))
                .thenReturn(CompletableFuture.supplyAsync(() -> model));
        storage = new Storage(mockClient, config);
        Object result = storage.getUserSetting(id).toCompletableFuture().get();
        JsonNode node = Json.toJson(result);
        assertEquals(node.get("Name").asText(), name);
        assertEquals(node.get("Description").asText(), description);
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void setUserSettingAsyncTest() throws BaseException, ExecutionException, InterruptedException {
        String id = this.rand.NextString();
        String name = rand.NextString();
        String description = rand.NextString();
        String jsonData = String.format("{\"Name\":\"%s\",\"Description\":\"%s\"}", name, description);
        Object setting = Json.fromJson(Json.parse(jsonData), Object.class);
        ValueApiModel model = new ValueApiModel();
        model.setData(jsonData);
        Mockito.when(mockClient.updateAsync(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class)))
                .thenReturn(CompletableFuture.supplyAsync(() -> model));
        storage = new Storage(mockClient, config);
        Object result = storage.setUserSetting(id, setting).toCompletableFuture().get();
        JsonNode node = Json.toJson(result);
        assertEquals(node.get("Name").asText(), name);
        assertEquals(node.get("Description").asText(), description);
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void getLogoAsyncTest() throws BaseException, ExecutionException, InterruptedException {
        String image = rand.NextString();
        String type = rand.NextString();
        String jsonData = String.format("{\"Image\":\"%s\",\"Type\":\"%s\"}", image, type);
        ValueApiModel model = new ValueApiModel();
        model.setData(jsonData);
        Mockito.when(mockClient.getAsync(Mockito.any(String.class), Mockito.any(String.class)))
                .thenReturn(CompletableFuture.supplyAsync(() -> model));
        storage = new Storage(mockClient, config);
        Object result = storage.getLogoAsync().toCompletableFuture().get();
        JsonNode node = Json.toJson(result);
        assertEquals(node.get("Image").asText(), image);
        assertEquals(node.get("Type").asText(), type);
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void setLogoAsyncTest() throws BaseException, ExecutionException, InterruptedException {
        String image = rand.NextString();
        String type = rand.NextString();
        Logo logo = new Logo();
        logo.setImage(image);
        logo.setType(type);
        ValueApiModel model = new ValueApiModel();
        model.setData(Json.stringify(Json.toJson(logo)));
        Mockito.when(mockClient.updateAsync(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class)))
                .thenReturn(CompletableFuture.supplyAsync(() -> model));
        storage = new Storage(mockClient, config);
        Object result = storage.setLogoAsync(logo).toCompletableFuture().get();
        JsonNode node = Json.toJson(result);
        assertEquals(node.get("Image").asText(), image);
        assertEquals(node.get("Type").asText(), type);
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void getAllDeviceGroupsAsyncTest() throws BaseException, ExecutionException, InterruptedException {
        List<DeviceGroup> groups = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            DeviceGroup model = new DeviceGroup();
            model.setDisplayName(rand.NextString());
            model.setConditions(null);
            groups.add(model);
        }
        List<ValueApiModel> items = groups.stream().map(m ->
                new ValueApiModel(rand.NextString(), Json.stringify(Json.toJson(m)), rand.NextString(), null)
        ).collect(Collectors.toList());
        ValueListApiModel model = new ValueListApiModel();
        model.Items = items;
        Mockito.when(mockClient.getAllAsync(Mockito.any(String.class))).thenReturn(CompletableFuture.supplyAsync(() -> model));
        storage = new Storage(mockClient, config);
        List<DeviceGroup> result = Lists.newArrayList(storage.getAllDeviceGroupsAsync().toCompletableFuture().get());
        assertEquals(result.size(), groups.size());
        for (DeviceGroup item : result) {
            ValueApiModel value = items.stream().filter(m -> m.getKey().equals(item.getId())).findFirst().get();
            DeviceGroup group = Json.fromJson(Json.parse(value.getData()), DeviceGroup.class);
            assertEquals(group.getDisplayName(), item.getDisplayName());
            assertEquals(group.getConditions(), item.getConditions());
        }
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void getDeviceGroupsAsyncTest() throws BaseException, ExecutionException, InterruptedException {
        String groupId = rand.NextString();
        String displayName = rand.NextString();
        Iterable<DeviceGroupCondition> conditions = null;
        String etag = rand.NextString();
        ValueApiModel model = new ValueApiModel(groupId, null, etag, null);
        DeviceGroup group = new DeviceGroup();
        group.setDisplayName(displayName);
        group.setConditions(conditions);
        model.setData(Json.stringify(Json.toJson(group)));
        Mockito.when(mockClient.getAsync(Mockito.any(String.class),
                Mockito.any(String.class))).
                thenReturn(CompletableFuture.supplyAsync(() -> model));
        storage = new Storage(mockClient, config);
        DeviceGroup result = storage.getDeviceGroupAsync(groupId).toCompletableFuture().get();
        assertEquals(result.getDisplayName(), displayName);
        assertEquals(result.getConditions(), conditions);
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void createDeviceGroupAsyncTest() throws BaseException, ExecutionException, InterruptedException {
        String groupId = rand.NextString();
        String displayName = rand.NextString();
        Iterable<DeviceGroupCondition> conditions = null;
        String etag = rand.NextString();
        ValueApiModel model = new ValueApiModel(groupId, null, etag, null);
        DeviceGroup group = new DeviceGroup();
        group.setConditions(conditions);
        group.setDisplayName(displayName);
        model.setData(Json.stringify(Json.toJson(group)));
        Mockito.when(mockClient.createAsync(Mockito.any(String.class),
                Mockito.any(String.class))).
                thenReturn(CompletableFuture.supplyAsync(() -> model));
        storage = new Storage(mockClient, config);
        DeviceGroup result = storage.createDeviceGroupAsync(group).toCompletableFuture().get();
        assertEquals(result.getId(), groupId);
        assertEquals(result.getDisplayName(), displayName);
        assertEquals(result.getConditions(), conditions);
        assertEquals(result.getETag(), etag);
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void updateDeviceGroupAsyncTest() throws BaseException, ExecutionException, InterruptedException {
        String groupId = rand.NextString();
        String displayName = rand.NextString();
        Iterable<DeviceGroupCondition> conditions = null;
        String etagOld = rand.NextString();
        String etagNew = rand.NextString();
        DeviceGroup group = new DeviceGroup();
        group.setDisplayName(displayName);
        group.setConditions(conditions);
        ValueApiModel model = new ValueApiModel(groupId, Json.stringify(Json.toJson(group)), etagNew, null);
        Mockito.when(mockClient.updateAsync(Mockito.any(String.class),
                Mockito.any(String.class),
                Mockito.any(String.class),
                Mockito.any(String.class))).
                thenReturn(CompletableFuture.supplyAsync(() -> model));
        storage = new Storage(mockClient, config);
        DeviceGroup result = storage.updateDeviceGroupAsync(groupId, group, etagOld).toCompletableFuture().get();
        assertEquals(result.getId(), groupId);
        assertEquals(result.getDisplayName(), displayName);
        assertEquals(result.getConditions(), conditions);
        assertEquals(result.getETag(), etagNew);
    }
}
