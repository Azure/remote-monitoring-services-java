// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.webservice.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.microsoft.azure.iotsolutions.uiconfig.services.IStorage;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.BaseException;
import com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.controllers.UserSettingsController;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.assertEquals;

public class UserSettingsControllerTest {

    private IStorage mockStorage;
    private UserSettingsController controller;
    private Random rand;

    @Before
    public void setUp() {
        mockStorage = Mockito.mock(IStorage.class);
        rand = new Random();
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void getUserSettingAsyncTest() throws BaseException, ExecutionException, InterruptedException {
        String id = this.rand.NextString();
        String name = rand.NextString();
        String description = rand.NextString();
        Object model = Json.fromJson(Json.parse(String.format("{\"Name\":\"%s\",\"Description\":\"%s\"}", name, description)), Object.class);
        Mockito.when(mockStorage.getUserSetting(Mockito.any(String.class))).thenReturn(CompletableFuture.supplyAsync(() -> model));
        controller = new UserSettingsController(mockStorage);
        String resultStr = TestUtils.getString(controller.getUserSettingAsync(id).toCompletableFuture().get());
        JsonNode result = Json.toJson(Json.parse(resultStr));
        assertEquals(result.get("Name").asText(), name);
        assertEquals(result.get("Description").asText(), description);
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void setUserSettingAsyncTest() throws BaseException, ExecutionException, InterruptedException {
        String id = this.rand.NextString();
        String name = rand.NextString();
        String description = rand.NextString();
        Object model = Json.fromJson(Json.parse(String.format("{\"Name\":\"%s\",\"Description\":\"%s\"}", name, description)), Object.class);
        Mockito.when(mockStorage.setUserSetting(Mockito.any(String.class), Mockito.any(Object.class))).thenReturn(CompletableFuture.supplyAsync(() -> model));
        controller = new UserSettingsController(mockStorage);
        TestUtils.setRequest(String.format("{\"Name\":\"%s\",\"Description\":\"%s\"}", name, description));
        String resultStr = TestUtils.getString(controller.setUserSettingAsync(id).toCompletableFuture().get());
        JsonNode result = Json.toJson(Json.parse(resultStr));
        assertEquals(result.get("Name").asText(), name);
        assertEquals(result.get("Description").asText(), description);
    }
}
