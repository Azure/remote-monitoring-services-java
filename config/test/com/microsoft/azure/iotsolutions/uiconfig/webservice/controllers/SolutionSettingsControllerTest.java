// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.webservice.controllers;

import akka.util.ByteString;
import com.fasterxml.jackson.databind.JsonNode;
import com.microsoft.azure.iotsolutions.uiconfig.services.IActions;
import com.microsoft.azure.iotsolutions.uiconfig.services.IStorage;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.BaseException;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.IAzureResourceManagerClient;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.Logo;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.actions.EmailActionSettings;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.actions.IActionSettings;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.controllers.SolutionSettingsController;
import com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.models.ActionSettingsApiModel;
import com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.models.ActionSettingsListApiModel;
import helpers.Random;
import helpers.TestUtils;
import helpers.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;

public class SolutionSettingsControllerTest {

    private IStorage mockStorage;
    private IActions mockActions;
    private IServicesConfig mockServicesConfig;
    private IAzureResourceManagerClient mockResourceManagementClient;
    private SolutionSettingsController controller;
    private Random rand;

    private static final String LOGO_BODY = "{\"Name\":\"1\"}";
    private static final int TIMEOUT = 100000;

    @Before
    public void setUp() {
        this.mockStorage = Mockito.mock(IStorage.class);
        this.mockActions = Mockito.mock(IActions.class);
        this.mockServicesConfig = Mockito.mock(IServicesConfig.class);
        this.mockResourceManagementClient = Mockito.mock(IAzureResourceManagerClient.class);
        rand = new Random();
    }

    @Test(timeout = SolutionSettingsControllerTest.TIMEOUT)
    @Category({UnitTest.class})
    public void getThemeAsyncTest() throws BaseException, ExecutionException, InterruptedException {
        String name = rand.NextString();
        String description = rand.NextString();
        Object model = Json.fromJson(Json.parse(String.format("{\"Name\":\"%s\",\"Description\":\"%s\"}", name, description)), Object.class);
        Mockito.when(mockStorage.getThemeAsync()).thenReturn(CompletableFuture.supplyAsync(() -> model));
        controller = new SolutionSettingsController(mockStorage, mockActions);
        String resultStr = TestUtils.getString(controller.getThemeAsync().toCompletableFuture().get());
        JsonNode result = Json.toJson(Json.parse(resultStr));
        assertEquals(result.get("Name").asText(), name);
        assertEquals(result.get("Description").asText(), description);
    }

    @Test(timeout = SolutionSettingsControllerTest.TIMEOUT)
    @Category({UnitTest.class})
    public void setThemeAsyncTest() throws ExecutionException, InterruptedException, BaseException {
        String name = rand.NextString();
        String description = rand.NextString();
        Object model = Json.fromJson(Json.parse(String.format("{\"Name\":\"%s\",\"Description\":\"%s\"}", name, description)), Object.class);
        Mockito.when(mockStorage.setThemeAsync(Mockito.any(Object.class))).thenReturn(CompletableFuture.supplyAsync(() -> model));
        controller = new SolutionSettingsController(mockStorage, mockActions);
        TestUtils.setRequest(String.format("{\"Name\":\"%s\",\"Description\":\"%s\"}", name, description));
        String resultStr = TestUtils.getString(controller.setThemeAsync().toCompletableFuture().get());
        JsonNode result = Json.toJson(Json.parse(resultStr));
        assertEquals(result.get("Name").asText(), name);
        assertEquals(result.get("Description").asText(), description);
    }

    @Test(timeout = SolutionSettingsControllerTest.TIMEOUT)
    @Category({UnitTest.class})
    public void getLogoShouldReturnExpectedNameAndType() throws BaseException, ExecutionException, InterruptedException {
        // Arrange
        String image = rand.NextString();
        String type = rand.NextString();
        String name = rand.NextString();
        Logo model = new Logo(image, type, name, false);
        getLogoMockSetup(model);
        controller = new SolutionSettingsController(mockStorage, mockActions);
        Http.Response mockResponse = TestUtils.setRequest(SolutionSettingsControllerTest.LOGO_BODY);

        // Act
        Result result = controller.getLogoAsync().toCompletableFuture().get();
        byte[] bytes = TestUtils.getBytes(result);
        byte[] bytesold = Base64.getDecoder().decode(model.getImage().getBytes());

        // Assert
        assertEquals(ByteString.fromArray(bytes), ByteString.fromArray(bytesold));
        Mockito.verify(mockResponse).setHeader(Logo.NAME_HEADER, name);
        Mockito.verify(mockResponse).setHeader(Logo.IS_DEFAULT_HEADER, Boolean.toString(false));
    }

    @Test(timeout = SolutionSettingsControllerTest.TIMEOUT)
    @Category({UnitTest.class})
    public void getLogoShouldReturnDefaultLogo() throws BaseException, ExecutionException, InterruptedException {
        // Arrange
        Logo model = Logo.Default;
        getLogoMockSetup(model);
        controller = new SolutionSettingsController(mockStorage, mockActions);
        Http.Response mockResponse = TestUtils.setRequest(SolutionSettingsControllerTest.LOGO_BODY);

        // Act
        Result result = controller.getLogoAsync().toCompletableFuture().get();
        byte[] bytes = TestUtils.getBytes(result);
        byte[] bytesold = Base64.getDecoder().decode(model.getImage().getBytes());

        // Assert
        assertEquals(ByteString.fromArray(bytes), ByteString.fromArray(bytesold));
        Mockito.verify(mockResponse).setHeader(Logo.NAME_HEADER, Logo.Default.getName());
        Mockito.verify(mockResponse).setHeader(Logo.IS_DEFAULT_HEADER, Boolean.toString(true));
    }

    @Test(timeout = SolutionSettingsControllerTest.TIMEOUT)
    @Category({UnitTest.class})
    public void setLogoShouldReturnGivenLogoAndName() throws BaseException, ExecutionException, InterruptedException, UnsupportedEncodingException, URISyntaxException {
        // Arrange
        String image = rand.NextString();
        String type = rand.NextString();
        String name = rand.NextString();
        Logo model = new Logo(image, type, name, false);
        setLogoMockSetup(model);
        controller = new SolutionSettingsController(mockStorage, mockActions);
        Http.Response mockResponse = TestUtils.setRequest(SolutionSettingsControllerTest.LOGO_BODY);

        // Act
        byte[] bytes = TestUtils.getBytes(controller.setLogoAsync().toCompletableFuture().get());
        byte[] bytesold = Base64.getDecoder().decode(model.getImage().getBytes());

        // Assert
        assertEquals(ByteString.fromArray(bytes), ByteString.fromArray(bytesold));
        Mockito.verify(mockResponse).setHeader(Logo.NAME_HEADER, name);
        Mockito.verify(mockResponse).setHeader(Logo.IS_DEFAULT_HEADER, Boolean.toString(false));
    }

    @Test(timeout = SolutionSettingsControllerTest.TIMEOUT)
    @Category({UnitTest.class})
    public void setLogoShouldReturnGivenLogo() throws BaseException, ExecutionException, InterruptedException, UnsupportedEncodingException, URISyntaxException {
        // Arrange
        String image = rand.NextString();
        String type = rand.NextString();
        Logo model = new Logo(image, type, null, false);
        setLogoMockSetup(model);
        controller = new SolutionSettingsController(mockStorage, mockActions);
        Http.Response mockResponse = TestUtils.setRequest(SolutionSettingsControllerTest.LOGO_BODY);

        // Act
        byte[] bytes = TestUtils.getBytes(controller.setLogoAsync().toCompletableFuture().get());
        byte[] bytesold = Base64.getDecoder().decode(model.getImage().getBytes());

        // Assert
        assertEquals(ByteString.fromArray(bytes), ByteString.fromArray(bytesold));
        Mockito.verify(mockResponse).setHeader(Logo.IS_DEFAULT_HEADER, Boolean.toString(false));
    }

    @Test(timeout = SolutionSettingsControllerTest.TIMEOUT)
    @Category({UnitTest.class})
    public void setLogoShouldReturnGivenName() throws BaseException, ExecutionException, InterruptedException, UnsupportedEncodingException, URISyntaxException {
        // Arrange
        String name = rand.NextString();
        Logo model = new Logo(null, null, name, false);
        setLogoMockSetup(model);
        controller = new SolutionSettingsController(mockStorage, mockActions);
        Http.Response mockResponse = TestUtils.setRequest(SolutionSettingsControllerTest.LOGO_BODY);

        // Act
        byte[] bytes = TestUtils.getBytes(controller.setLogoAsync().toCompletableFuture().get());
        byte[] emptyBytes = new byte[0];

        // Assert
        assertEquals(ByteString.fromArray(bytes), ByteString.fromArray(emptyBytes));
        Mockito.verify(mockResponse).setHeader(Logo.NAME_HEADER, name);
        Mockito.verify(mockResponse).setHeader(Logo.IS_DEFAULT_HEADER, Boolean.toString(false));
    }

    @Test(timeout = SolutionSettingsControllerTest.TIMEOUT)
    @Category({UnitTest.class})
    public void getActionsReturnsListOfActions() throws BaseException, ExecutionException, InterruptedException {
        // Arrange
        this.controller = new SolutionSettingsController(mockStorage, mockActions);

        IActionSettings action = new EmailActionSettings(this.mockResourceManagementClient, this.mockServicesConfig);
        ArrayList<IActionSettings> actionSettings = new ArrayList<>();
        actionSettings.add(action);
        Mockito.when(mockActions.getList()).thenReturn(actionSettings);

        // Act
        String resultStr = TestUtils.getString(this.controller.getActionsSettings());
        ActionSettingsListApiModel result = Json.fromJson(Json.parse(resultStr), ActionSettingsListApiModel.class);

        // Assert
        assertFalse((((ArrayList<ActionSettingsApiModel>)result.getItems()).isEmpty()));
        assertFalse(result.getMetadata().isEmpty());
    }

    private void setLogoMockSetup(Logo model) throws BaseException{
        Mockito.when(mockStorage.setLogoAsync(Mockito.any(Logo.class))).thenReturn(CompletableFuture.supplyAsync(() -> model));
    }

    private void getLogoMockSetup(Logo model) throws BaseException{
        Mockito.when(mockStorage.getLogoAsync()).thenReturn(CompletableFuture.supplyAsync(() -> model));
    }
}
