// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.BaseException;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.ResourceNotFoundException;
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

import static org.junit.Assert.*;

public class StorageTest {

    private IStorageAdapterClient mockClient;
    private Storage storage;
    private Random rand;
    private ServicesConfig config;
    private String azureMapsKey;
    private static final String LOGO_FORMAT = "{\"Image\":\"%s\",\"Type\":\"%s\",\"Name\":\"%s\",\"IsDefault\":%s}";
    private static final int TIMEOUT = 100000;

    @Before
    public void setUp() throws URISyntaxException, IOException {
        mockClient = Mockito.mock(IStorageAdapterClient.class);
        rand = new Random();
        config = new ServicesConfig();
        azureMapsKey = rand.NextString();
        config.setAzureMapsKey(azureMapsKey);
    }

    @Test(timeout = StorageTest.TIMEOUT)
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
        assertEquals(node.get("AzureMapsKey").asText(), azureMapsKey);
    }

    @Test(timeout = StorageTest.TIMEOUT)
    @Category({UnitTest.class})
    public void getThemeAsyncDefaultTest() throws BaseException, ExecutionException, InterruptedException {
        Mockito.when(mockClient.getAsync(Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new BaseException());
        storage = new Storage(mockClient, config);
        Object result = storage.getThemeAsync().toCompletableFuture().get();
        JsonNode node = Json.toJson(result);
        assertEquals(node.get("Name").asText(), Theme.Default.getName());
        assertEquals(node.get("Description").asText(), Theme.Default.getDescription());
        assertEquals(node.get("AzureMapsKey").asText(), azureMapsKey);
    }

    @Test(timeout = StorageTest.TIMEOUT)
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
        assertEquals(node.get("AzureMapsKey").asText(), azureMapsKey);
    }

    @Test(timeout = StorageTest.TIMEOUT)
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

    @Test(timeout = StorageTest.TIMEOUT)
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

    @Test(timeout = StorageTest.TIMEOUT)
    @Category({UnitTest.class})
    public void getLogoShouldReturnExpectedLogo() throws BaseException, ExecutionException, InterruptedException {
        // Arrange
        String image = rand.NextString();
        String type = rand.NextString();
        String isDefault = "false";
        String jsonData = String.format("{\"Image\":\"%s\",\"Type\":\"%s\",\"IsDefault\":%s}", image, type, isDefault);
        mockGetLogo(jsonData);
        storage = new Storage(mockClient, config);

        // Act
        Object result = storage.getLogoAsync().toCompletableFuture().get();
        JsonNode node = Json.toJson(result);

        // Assert
        assertEquals(node.get("Image").asText(), image);
        assertEquals(node.get("Type").asText(), type);
        assertFalse(node.get("IsDefault").booleanValue());
    }

    @Test(timeout = StorageTest.TIMEOUT)
    @Category({UnitTest.class})
    public void getLogoShouldReturnExpectedLogoAndName() throws BaseException, ExecutionException, InterruptedException {
        // Arrange
        String image = rand.NextString();
        String type = rand.NextString();
        String name = rand.NextString();
        String isDefault = "false";
        String jsonData = String.format(StorageTest.LOGO_FORMAT, image, type, name, isDefault);
        mockGetLogo(jsonData);
        storage = new Storage(mockClient, config);

        // Act
        Object result = storage.getLogoAsync().toCompletableFuture().get();
        JsonNode node = Json.toJson(result);

        // Assert
        assertEquals(node.get("Image").asText(), image);
        assertEquals(node.get("Type").asText(), type);
        assertEquals(node.get("Name").asText(), name);
        assertFalse(node.get("IsDefault").booleanValue());
    }

    @Test(timeout = StorageTest.TIMEOUT)
    @Category({UnitTest.class})
    public void getLogoShouldReturnDefaultLogoOnException() throws BaseException, ExecutionException, InterruptedException {
        // Arrange
        Mockito.when(mockClient.getAsync(Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new ResourceNotFoundException());
        storage = new Storage(mockClient, config);

        // Act
        Object result = storage.getLogoAsync().toCompletableFuture().get();
        JsonNode node = Json.toJson(result);

        // Assert
        assertEquals(Logo.Default.getImage(), node.get("Image").asText());
        assertEquals(Logo.Default.getType(),node.get("Type").asText());
        assertEquals(Logo.Default.getName(), node.get("Name").asText());
        assertTrue(node.get("IsDefault").booleanValue());
    }

    @Test(timeout = StorageTest.TIMEOUT)
    @Category({UnitTest.class})
    public void setLogoShouldNotOverwriteOldNameWithNull() throws BaseException, ExecutionException, InterruptedException {
        // Arrange
        String image = rand.NextString();
        String type = rand.NextString();
        Logo logo = new Logo(image, type, null, false);
        String oldName = rand.NextString();

        // Act
        JsonNode node = SetLogoHelper(logo, oldName);

        // Assert
        assertEquals(image, node.get("Image").asText());
        assertEquals(type, node.get("Type").asText());
        assertEquals(oldName, node.get("Name").asText());
        assertFalse(node.get("IsDefault").booleanValue());
    }

    @Test(timeout = StorageTest.TIMEOUT)
    @Category({UnitTest.class})
    public void setLogoShouldSetAllPartsOfLogoIfNotNull() throws BaseException, ExecutionException, InterruptedException {
        // Arrange
        String image = rand.NextString();
        String type = rand.NextString();
        String name = rand.NextString();
        Logo logo = new Logo(image, type, name, false);

        // Act
        JsonNode node = SetLogoHelper(logo, rand.NextString());

        // Assert
        assertEquals(image, node.get("Image").asText());
        assertEquals(type, node.get("Type").asText());
        assertEquals(name, node.get("Name").asText());
        assertFalse(node.get("IsDefault").booleanValue());
    }

    private JsonNode SetLogoHelper(Logo logo, String oldName) throws BaseException, ExecutionException, InterruptedException {
        mockSetLogo();
        String oldImage = rand.NextString();
        String oldType = rand.NextString();
        String isDefault = "false";
        String jsonData = String.format(StorageTest.LOGO_FORMAT, oldImage, oldType, oldName, isDefault);
        mockGetLogo(jsonData);
        storage = new Storage(mockClient, config);
        Object result = storage.setLogoAsync(logo).toCompletableFuture().get();
        return Json.toJson(result);
    }

    private void mockSetLogo() throws BaseException{
        Mockito.when(mockClient.updateAsync(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class)))
                .thenAnswer(i -> CompletableFuture.supplyAsync(() -> new ValueApiModel(null, i.getArgument(2), null, null)));
    }

    private void mockGetLogo(String jsonData) throws BaseException {
        ValueApiModel model = new ValueApiModel();
        model.setData(jsonData);
        Mockito.when(mockClient.getAsync(Mockito.any(String.class), Mockito.any(String.class)))
                .thenReturn(CompletableFuture.supplyAsync(() -> model));
    }

    @Test(timeout = StorageTest.TIMEOUT)
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

    @Test(timeout = StorageTest.TIMEOUT)
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

    @Test(timeout = StorageTest.TIMEOUT)
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

    @Test(timeout = StorageTest.TIMEOUT)
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
