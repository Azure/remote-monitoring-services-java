// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.BaseException;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.ResourceNotFoundException;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.IStorageAdapterClient;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.ValueApiModel;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.ValueListApiModel;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.*;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.Package;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.ServicesConfig;
import helpers.Random;
import helpers.UnitTest;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
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

import static org.joda.time.format.DateTimeFormat.forPattern;
import static org.junit.Assert.*;

public class StorageTest {

    private IStorageAdapterClient mockClient;
    private Storage storage;
    private Random rand;
    private ServicesConfig config;
    private String azureMapsKey;
    private static final String LOGO_FORMAT = "{\"Image\":\"%s\",\"Type\":\"%s\",\"Name\":\"%s\",\"IsDefault\":%s}";
    private static final DateTimeFormatter DATE_FORMAT =
            forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");
    private static final String DEVICE_GROUPS_COLLECTION_ID = "devicegroups";
    private static final String PACKAGES_COLLECTION_ID = "packages";
    private static final int TIMEOUT = 100000;

    @Before
    public void setUp() throws URISyntaxException, IOException {
        mockClient = Mockito.mock(IStorageAdapterClient.class);
        rand = new Random();
        config = new ServicesConfig();
        azureMapsKey = rand.NextString();
        config.setAzureMapsKey(azureMapsKey);
        this.storage = new Storage(mockClient, config);
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
        this.setupAllAsyncMock(DEVICE_GROUPS_COLLECTION_ID, items);

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
        DeviceGroup result = storage.updateDeviceGroupAsync(groupId, group, etagOld).toCompletableFuture().get();
        assertEquals(result.getId(), groupId);
        assertEquals(result.getDisplayName(), displayName);
        assertEquals(result.getConditions(), conditions);
        assertEquals(result.getETag(), etagNew);
    }

    @Test
    @Category({UnitTest.class})
    public void addPackageTest() throws BaseException, ExecutionException, InterruptedException {
        Package pkg = new Package(null, rand.NextString(), PackageType.edgeManifest, rand.NextString());

        ValueApiModel model = new ValueApiModel(rand.NextString(), null, null, null);
        model.setData(Json.stringify(Json.toJson(pkg)));

        Mockito.when(mockClient.createAsync(Mockito.eq(PACKAGES_COLLECTION_ID),
                                            Mockito.any(String.class)))
               .thenReturn(CompletableFuture.supplyAsync(() -> model));

        Package result = storage.addPackageAsync(pkg).toCompletableFuture().get();
        assertEquals(pkg.getName(), result.getName());
        assertEquals(pkg.getType(), result.getType());
        assertEquals(pkg.getContent(), result.getContent());
    }

    @Test(timeout = StorageTest.TIMEOUT)
    @Category({UnitTest.class})
    public void getAllPackageTest() throws BaseException, ExecutionException, InterruptedException {
        List<Package> packages = new ArrayList<>();
        final String pkgName = "pkgName";
        final PackageType type = PackageType.edgeManifest;
        final String content = "{}";
        final String dateCreated =
                StorageTest.DATE_FORMAT.print(DateTime.now().toDateTime(DateTimeZone.UTC));

        for (int i = 0; i < 5; i++) {
            Package model = new Package(null, pkgName + i, type, content + i, dateCreated);
            packages.add(model);
        }

        List<ValueApiModel> items = packages.stream().map(m ->
                new ValueApiModel(rand.NextString(), Json.stringify(Json.toJson(m)), rand.NextString(), null)
        ).collect(Collectors.toList());
        this.setupAllAsyncMock(PACKAGES_COLLECTION_ID, items);

        List<Package> results = Lists.newArrayList(storage.getAllPackagesAsync().toCompletableFuture().get());
        assertEquals(5, results.size());

        for (int i = 0; i < results.size(); i++) {
            Package result = results.get(i);
            ValueApiModel value = items.stream().filter(m -> m.getKey().equals(result.getId())).findFirst().get();
            Package pkg = Json.fromJson(Json.parse(value.getData()), Package.class);
            assertEquals(pkgName + i, pkg.getName());
            assertEquals(content + i, pkg.getContent());

        }
    }

    @Test(timeout = StorageTest.TIMEOUT)
    @Category({UnitTest.class})
    public void deletePackageTest() throws BaseException {
        final String packageId = rand.NextString();
        this.storage.deletePackageAsync(packageId);

        Mockito.verify(this.mockClient).deleteAsync(PACKAGES_COLLECTION_ID, packageId);
    }

    private void setupAllAsyncMock(String collectionId, List<ValueApiModel> items) throws BaseException {
        ValueListApiModel model = new ValueListApiModel();
        model.Items = items;
        Mockito.when(mockClient.getAllAsync(Mockito.eq(collectionId)))
                .thenReturn(CompletableFuture.supplyAsync(() -> model));
    }
}
