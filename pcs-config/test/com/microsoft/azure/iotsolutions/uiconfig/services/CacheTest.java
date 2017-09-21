// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services;

import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.BaseException;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.*;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.CacheValue;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.ServicesConfig;
import helpers.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;
import play.libs.Json;

import java.util.Arrays;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

public class CacheTest {
    private IStorageAdapterClient mockStorageAdapterClient;
    private IIothubManagerServiceClient mockIothubManagerClient;
    private ISimulationServiceClient mockSimulationClient;
    private String cacheModel = "";
    private IServicesConfig config;
    private Cache cache;

    @Before
    public void setUp() {
        mockStorageAdapterClient = Mockito.mock(IStorageAdapterClient.class);
        mockIothubManagerClient = Mockito.mock(IIothubManagerServiceClient.class);
        mockSimulationClient = Mockito.mock(ISimulationServiceClient.class);
        cacheModel = "{\"Rebuilding\": false,\"Tags\": [ \"c\", \"a\", \"y\", \"z\" ],\"Reported\": [\"1\",\"9\",\"2\",\"3\"] }";
        config = new ServicesConfig(null, null, null, 0, 0);
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void getCacheAsyncTestAsync() throws BaseException, ExecutionException, InterruptedException {
        Mockito.when(mockStorageAdapterClient.getAsync(Mockito.any(String.class), Mockito.any(String.class)))
                .thenReturn(CompletableFuture.supplyAsync(() -> new ValueApiModel("", this.cacheModel, "", null)));
        cache = new Cache(mockStorageAdapterClient, mockIothubManagerClient, mockSimulationClient, config);
        CacheValue result = this.cache.GetCacheAsync().toCompletableFuture().get();
        assertEquals(String.join(",", new TreeSet<String>(result.getTags())), "a,c,y,z");
        assertEquals(String.join(",", new TreeSet<String>(result.getReported())), "1,2,3,9");
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void setCacheAsyncTestAsync() throws BaseException, ExecutionException, InterruptedException {
        Mockito.when(mockStorageAdapterClient.getAsync(Mockito.any(String.class), Mockito.any(String.class)))
                .thenReturn(CompletableFuture.supplyAsync(() -> new ValueApiModel("", this.cacheModel, "", null)));
        CacheValue resultModel = new CacheValue(new HashSet<String>(Arrays.asList("c", "a", "y", "z", "@", "#")),
                new HashSet<String>(Arrays.asList("1", "9", "2", "3", "12", "11")), false);
        CacheValue model = new CacheValue(new HashSet<String>(Arrays.asList("a", "y", "z", "@", "#")),
                new HashSet<String>(Arrays.asList("9", "2", "3", "11", "12")), false);
        Mockito.when(mockStorageAdapterClient.updateAsync(Mockito.any(String.class), Mockito.any(String.class),
                Mockito.any(String.class), Mockito.any(String.class)))
                .thenReturn(CompletableFuture.supplyAsync(() -> new ValueApiModel("", Json.stringify(Json.toJson(resultModel)), "", null)));
        cache = new Cache(mockStorageAdapterClient, mockIothubManagerClient, mockSimulationClient, config);
        CacheValue result = this.cache.SetCacheAsync(model).toCompletableFuture().get();
        assertEquals(String.join(",", new TreeSet<String>(result.getTags())), "#,@,a,c,y,z");
        assertEquals(String.join(",", new TreeSet<String>(result.getReported())), "1,11,12,2,3,9");
    }
}
