// Copyright (c) Microsoft. All rights reserved.
package com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.microsoft.azure.iotsolutions.iothubmanager.services.IDeviceProperties;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.BaseException;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models.DevicePropertiesApiModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import play.mvc.Result;

import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;

import static play.libs.Json.toJson;

public class DevicePropertiesControllerTest {
    private DevicePropertiesController devicePropertiesController;
    private IDeviceProperties devicePropertiesMock;
    private static final int TIMEOUT_MSEC = 100000;

    @Before
    public void setUp() {
        this.devicePropertiesMock = Mockito.mock(IDeviceProperties.class);
        this.devicePropertiesController = new DevicePropertiesController(this.devicePropertiesMock);
    }

    @Test(timeout = TIMEOUT_MSEC)
    public void GetPropertiesReturnsExceptedResponse() throws BaseException {
        // Arrange
        TreeSet<String> fakeList = new TreeSet<>();
        fakeList.add("property1");
        fakeList.add("property2");
        fakeList.add("property3");
        fakeList.add("property4");

        Mockito.when(this.devicePropertiesMock.getListAsync())
                .thenReturn(CompletableFuture.supplyAsync(() -> fakeList));

        DevicePropertiesApiModel expectedModel = new DevicePropertiesApiModel(fakeList);
        JsonNode expectedJson = toJson(expectedModel);

        // Act
        this.devicePropertiesController.getAllAsync().thenApply((Result result) ->
                {
                    // Assert
                    Assert.assertEquals(200, result.status());
                    Assert.assertFalse(result.body().isKnownEmpty());
                    Assert.assertEquals(expectedJson, result.body().as("application/json"));
                    return null;
                }
        );
    }
}
