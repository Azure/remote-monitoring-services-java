// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.IStorageAdapterClient;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.ValueApiModel;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.ValueListApiModel;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.DeviceGroupServiceModel;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.LogoServiceModel;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.ThemeServiceModel;
import helpers.Random;
import helpers.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;
import play.libs.Json;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

    @Before
    public void setUp() throws URISyntaxException, IOException {
        mockClient = Mockito.mock(IStorageAdapterClient.class);
        rand = new Random();
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void getThemeAsyncTest() throws UnsupportedEncodingException, URISyntaxException, ExecutionException, InterruptedException {
        String name = rand.NextString();
        String description = rand.NextString();
        ValueApiModel model = new ValueApiModel();
        model.setData(String.format("{\"name\":\"%s\",\"description\":\"%s\"}", name, description));
        Mockito.when(mockClient.getAsync(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(CompletableFuture.supplyAsync(() -> model));
        storage = new Storage(mockClient);
        Object result = storage.getThemeAsync().toCompletableFuture().get();
        JsonNode node = Json.toJson(result);
        assertEquals(node.get("name").asText(), name);
        assertEquals(node.get("description").asText(), description);
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void getThemeAsyncDefaultTest() throws UnsupportedEncodingException, URISyntaxException, ExecutionException, InterruptedException {
        Mockito.when(mockClient.getAsync(Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new UnsupportedEncodingException());
        storage = new Storage(mockClient);
        Object result = storage.getThemeAsync().toCompletableFuture().get();
        JsonNode node = Json.toJson(result);
        assertEquals(node.get("name").asText(), ThemeServiceModel.Default.getName());
        assertEquals(node.get("description").asText(), ThemeServiceModel.Default.getDescription());
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void setThemeAsyncTest() throws UnsupportedEncodingException, URISyntaxException, ExecutionException, InterruptedException {
        String name = rand.NextString();
        String description = rand.NextString();
        String jsonData = String.format("{\"name\":\"%s\",\"description\":\"%s\"}", name, description);
        Object theme = Json.fromJson(Json.parse(jsonData), Object.class);
        ValueApiModel model = new ValueApiModel();
        model.setData(jsonData);
        Mockito.when(mockClient.updateAsync(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).
                thenReturn(CompletableFuture.supplyAsync(() -> model));
        storage = new Storage(mockClient);
        Object result = storage.setThemeAsync(theme).toCompletableFuture().get();
        JsonNode node = Json.toJson(result);
        assertEquals(node.get("name").asText(), name);
        assertEquals(node.get("description").asText(), description);
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void getUserSettingAsyncTest() throws UnsupportedEncodingException, URISyntaxException, ExecutionException, InterruptedException {
        String id = this.rand.NextString();
        String name = rand.NextString();
        String description = rand.NextString();
        String jsonData = String.format("{\"name\":\"%s\",\"description\":\"%s\"}", name, description);
        Object data = Json.fromJson(Json.parse(jsonData), Object.class);
        ValueApiModel model = new ValueApiModel();
        model.setData(jsonData);
        Mockito.when(mockClient.getAsync(Mockito.any(String.class), Mockito.any(String.class)))
                .thenReturn(CompletableFuture.supplyAsync(() -> model));
        storage = new Storage(mockClient);
        Object result = storage.getUserSetting(id).toCompletableFuture().get();
        JsonNode node = Json.toJson(result);
        assertEquals(node.get("name").asText(), name);
        assertEquals(node.get("description").asText(), description);
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void setUserSettingAsyncTest() throws UnsupportedEncodingException, URISyntaxException, ExecutionException, InterruptedException {
        String id = this.rand.NextString();
        String name = rand.NextString();
        String description = rand.NextString();
        String jsonData = String.format("{\"name\":\"%s\",\"description\":\"%s\"}", name, description);
        Object setting = Json.fromJson(Json.parse(jsonData), Object.class);
        ValueApiModel model = new ValueApiModel();
        model.setData(jsonData);
        Mockito.when(mockClient.updateAsync(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class)))
                .thenReturn(CompletableFuture.supplyAsync(() -> model));
        storage = new Storage(mockClient);
        Object result = storage.setUserSetting(id, setting).toCompletableFuture().get();
        JsonNode node = Json.toJson(result);
        assertEquals(node.get("name").asText(), name);
        assertEquals(node.get("description").asText(), description);
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void getLogoAsyncTest() throws UnsupportedEncodingException, URISyntaxException, ExecutionException, InterruptedException {
        String image = rand.NextString();
        String type = rand.NextString();
        String jsonData = String.format("{\"image\":\"%s\",\"type\":\"%s\"}", image, type);
        ValueApiModel model = new ValueApiModel();
        model.setData(jsonData);
        Mockito.when(mockClient.getAsync(Mockito.any(String.class), Mockito.any(String.class)))
                .thenReturn(CompletableFuture.supplyAsync(() -> model));
        storage = new Storage(mockClient);
        Object result = storage.getLogoAsync().toCompletableFuture().get();
        JsonNode node = Json.toJson(result);
        assertEquals(node.get("image").asText(), image);
        assertEquals(node.get("type").asText(), type);
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void setLogoAsyncTest() throws UnsupportedEncodingException, URISyntaxException, ExecutionException, InterruptedException {
        String image = rand.NextString();
        String type = rand.NextString();
        LogoServiceModel logo = new LogoServiceModel();
        logo.setImage(image);
        logo.setType(type);
        ValueApiModel model = new ValueApiModel();
        model.setData(Json.stringify(Json.toJson(logo)));
        Mockito.when(mockClient.updateAsync(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class)))
                .thenReturn(CompletableFuture.supplyAsync(() -> model));
        storage = new Storage(mockClient);
        Object result = storage.setLogoAsync(logo).toCompletableFuture().get();
        JsonNode node = Json.toJson(result);
        assertEquals(node.get("image").asText(), image);
        assertEquals(node.get("type").asText(), type);
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void getAllDeviceGroupsAsyncTest() throws UnsupportedEncodingException, URISyntaxException, ExecutionException, InterruptedException {
        List<DeviceGroupServiceModel> groups = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            DeviceGroupServiceModel model = new DeviceGroupServiceModel();
            model.setDisplayName(rand.NextString());
            model.setConditions(rand.NextString());
            groups.add(model);
        }
        List<ValueApiModel> items = groups.stream().map(m ->
                new ValueApiModel(rand.NextString(), Json.stringify(Json.toJson(m)), rand.NextString(), null)
        ).collect(Collectors.toList());
        ValueListApiModel model = new ValueListApiModel();
        model.Items = items;
        Mockito.when(mockClient.getAllAsync(Mockito.any(String.class))).thenReturn(CompletableFuture.supplyAsync(() -> model));
        storage = new Storage(mockClient);
        List<DeviceGroupServiceModel> result = Lists.newArrayList(storage.getAllDeviceGroupsAsync().toCompletableFuture().get());
        assertEquals(result.size(), groups.size());
        for (DeviceGroupServiceModel item : result) {
            ValueApiModel value = items.stream().filter(m -> m.getKey().equals(item.getId())).findFirst().get();
            DeviceGroupServiceModel group = Json.fromJson(Json.parse(value.getData()), DeviceGroupServiceModel.class);
            assertEquals(group.getDisplayName(), item.getDisplayName());
            assertEquals(group.getConditions(), item.getConditions());
        }
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void getDeviceGroupsAsyncTest() throws UnsupportedEncodingException, URISyntaxException, ExecutionException, InterruptedException {
        String groupId = rand.NextString();
        String displayName = rand.NextString();
        String conditions = rand.NextString();
        String etag = rand.NextString();
        ValueApiModel model = new ValueApiModel(groupId, null, etag, null);
        DeviceGroupServiceModel group = new DeviceGroupServiceModel();
        group.setDisplayName(displayName);
        group.setConditions(conditions);
        model.setData(Json.stringify(Json.toJson(group)));
        Mockito.when(mockClient.getAsync(Mockito.any(String.class),
                Mockito.any(String.class))).
                thenReturn(CompletableFuture.supplyAsync(() -> model));
        storage = new Storage(mockClient);
        DeviceGroupServiceModel result = storage.getDeviceGroupAsync(groupId).toCompletableFuture().get();
        assertEquals(result.getDisplayName(), displayName);
        assertEquals(result.getConditions(), conditions);
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void createDeviceGroupAsyncTest() throws UnsupportedEncodingException, URISyntaxException, ExecutionException, InterruptedException {
        String groupId = rand.NextString();
        String displayName = rand.NextString();
        String conditions = rand.NextString();
        String etag = rand.NextString();
        ValueApiModel model = new ValueApiModel(groupId, null, etag, null);
        DeviceGroupServiceModel group = new DeviceGroupServiceModel();
        group.setConditions(conditions);
        group.setDisplayName(displayName);
        model.setData(Json.stringify(Json.toJson(group)));
        Mockito.when(mockClient.createAsync(Mockito.any(String.class),
                Mockito.any(String.class))).
                thenReturn(CompletableFuture.supplyAsync(() -> model));
        storage = new Storage(mockClient);
        DeviceGroupServiceModel result = storage.createDeviceGroupAsync(group).toCompletableFuture().get();
        assertEquals(result.getId(), groupId);
        assertEquals(result.getDisplayName(), displayName);
        assertEquals(result.getConditions(), conditions);
        assertEquals(result.geteTag(), etag);
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void updateDeviceGroupAsyncTest() throws UnsupportedEncodingException, URISyntaxException, ExecutionException, InterruptedException {
        String groupId = rand.NextString();
        String displayName = rand.NextString();
        String conditions = rand.NextString();
        String etagOld = rand.NextString();
        String etagNew = rand.NextString();
        DeviceGroupServiceModel group = new DeviceGroupServiceModel();
        group.setDisplayName(displayName);
        group.setConditions(conditions);
        ValueApiModel model = new ValueApiModel(groupId, Json.stringify(Json.toJson(group)), etagNew, null);
        Mockito.when(mockClient.updateAsync(Mockito.any(String.class),
                Mockito.any(String.class),
                Mockito.any(String.class),
                Mockito.any(String.class))).
                thenReturn(CompletableFuture.supplyAsync(() -> model));
        storage = new Storage(mockClient);
        DeviceGroupServiceModel result = storage.updateDeviceGroupAsync(groupId, group, etagOld).toCompletableFuture().get();
        assertEquals(result.getId(), groupId);
        assertEquals(result.getDisplayName(), displayName);
        assertEquals(result.getConditions(), conditions);
        assertEquals(result.geteTag(), etagNew);
    }
}
