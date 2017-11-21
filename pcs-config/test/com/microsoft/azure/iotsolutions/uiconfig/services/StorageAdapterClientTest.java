// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services;

import com.google.common.collect.Lists;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.*;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.IStorageAdapterClient;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.StorageAdapterClient;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.ValueApiModel;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.ValueListApiModel;
import com.microsoft.azure.iotsolutions.uiconfig.services.http.HttpRequest;
import com.microsoft.azure.iotsolutions.uiconfig.services.http.HttpResponse;
import com.microsoft.azure.iotsolutions.uiconfig.services.http.IHttpClient;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.ServicesConfig;
import helpers.Random;
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

import static org.junit.Assert.assertEquals;

public class StorageAdapterClientTest {

    private String MockServiceUri = "http://mockstorageadapter";
    private IHttpClient mockHttpClient;
    private IStorageAdapterClient client;
    private Random rand;

    @Before
    public void setUp() {
        mockHttpClient = Mockito.mock(IHttpClient.class);
        rand = new Random();
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void getAsyncTest() throws BaseException, ExecutionException, InterruptedException {
        String collectionId = rand.NextString();
        String key = rand.NextString();
        String data = rand.NextString();
        String etag = rand.NextString();
        ValueApiModel model = new ValueApiModel(key, data, etag, null);
        HttpResponse response = new HttpResponse();
        response.setContent(Json.stringify(Json.toJson(model)));
        response.setStatusCode(200);
        Mockito.when(mockHttpClient.getAsync(Mockito.any(HttpRequest.class)))
                .thenReturn(CompletableFuture.supplyAsync(() -> response));
        client = new StorageAdapterClient(
                mockHttpClient,
                new ServicesConfig(null, MockServiceUri, null, null, 0, 0, null, null, null));
        ValueApiModel result = client.getAsync(collectionId, key)
                .toCompletableFuture().get();
        assertEquals(result.getData(), data);
        assertEquals(result.getKey(), key);
        assertEquals(result.getETag(), etag);
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void getAsyncNotFoundTest() {
        String collectionId = rand.NextString();
        String key = rand.NextString();
        HttpResponse response = new HttpResponse();
        response.setStatusCode(404);
        Mockito.when(mockHttpClient.getAsync(Mockito.any(HttpRequest.class)))
                .thenReturn(CompletableFuture.supplyAsync(() -> response));
        client = new StorageAdapterClient(
                mockHttpClient,
                new ServicesConfig(null, MockServiceUri, null, null, 0, 0, null, null, null));
        try {
            client.getAsync(collectionId, key).toCompletableFuture().get();
        } catch (Exception e) {
            assertEquals(e.getClass(), ResourceNotFoundException.class);
        }
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void getAllAsyncTest() throws BaseException, ExecutionException, InterruptedException {
        String collectionId = rand.NextString();
        List<ValueApiModel> models = new ArrayList<ValueApiModel>();
        for (int i = 0; i < 5; i++) {
            ValueApiModel model = new ValueApiModel(rand.NextString(), rand.NextString(), rand.NextString(), null);
            models.add(model);
        }
        ValueListApiModel listApiModel = new ValueListApiModel();
        listApiModel.Items = models;
        HttpResponse response = new HttpResponse();
        response.setStatusCode(200);
        response.setContent(Json.stringify(Json.toJson(listApiModel)));
        Mockito.when(mockHttpClient.getAsync(Mockito.any(HttpRequest.class)))
                .thenReturn(CompletableFuture.supplyAsync(() -> response));
        client = new StorageAdapterClient(
                mockHttpClient,
                new ServicesConfig(null, MockServiceUri, null, null, 0, 0, null, null, null));
        ValueListApiModel result = client.getAllAsync(collectionId).toCompletableFuture().get();
        assertEquals(Lists.newArrayList(result.Items).size(), models.size());
        for (ValueApiModel item : result.Items) {
            ValueApiModel model = models.stream().filter(m -> m.getKey().equals(item.getKey())).findFirst().get();
            assertEquals(model.getData(), item.getData());
            assertEquals(model.getETag(), item.getETag());
        }
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void createAsyncTest() throws BaseException, ExecutionException, InterruptedException {
        String collectionId = rand.NextString();
        String key = rand.NextString();
        String data = rand.NextString();
        String etag = rand.NextString();
        HttpResponse response = new HttpResponse();
        response.setStatusCode(200);
        ValueApiModel model = new ValueApiModel(key, data, etag, null);
        response.setContent(Json.stringify(Json.toJson(model)));
        Mockito.when(mockHttpClient.postAsync(Mockito.any(HttpRequest.class)))
                .thenReturn(CompletableFuture.supplyAsync(() -> response));
        client = new StorageAdapterClient(
                mockHttpClient,
                new ServicesConfig(null, MockServiceUri, null, null, 0, 0, null, null, null));
        ValueApiModel result = client.createAsync(collectionId, data).toCompletableFuture().get();
        assertEquals(result.getKey(), key);
        assertEquals(result.getData(), data);
        assertEquals(result.getETag(), etag);
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void updateAsyncTest() throws BaseException, ExecutionException, InterruptedException {
        String collectionId = rand.NextString();
        String key = rand.NextString();
        String data = rand.NextString();
        String etagOld = rand.NextString();
        String etagNew = rand.NextString();
        HttpResponse response = new HttpResponse();
        response.setStatusCode(200);
        ValueApiModel model = new ValueApiModel(key, data, etagNew, null);
        response.setContent(Json.stringify(Json.toJson(model)));
        Mockito.when(mockHttpClient.putAsync(Mockito.any(HttpRequest.class)))
                .thenReturn(CompletableFuture.supplyAsync(() -> response));
        client = new StorageAdapterClient(
                mockHttpClient,
                new ServicesConfig(null, MockServiceUri, null, null, 0, 0, null, null, null));
        ValueApiModel result = client.updateAsync(collectionId, key, data, etagOld).toCompletableFuture().get();
        assertEquals(result.getKey(), key);
        assertEquals(result.getData(), data);
        assertEquals(result.getETag(), etagNew);
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void updateAsyncConflictTest() throws BaseException, ExecutionException, InterruptedException {
        String collectionId = rand.NextString();
        String key = rand.NextString();
        String data = rand.NextString();
        String etag = rand.NextString();
        HttpResponse response = new HttpResponse();
        response.setStatusCode(409);
        Mockito.when(mockHttpClient.putAsync(Mockito.any(HttpRequest.class)))
                .thenReturn(CompletableFuture.supplyAsync(() -> response));
        client = new StorageAdapterClient(
                mockHttpClient,
                new ServicesConfig(null, MockServiceUri, null, null, 0, 0, null, null, null));
        try {
            client.updateAsync(collectionId, key, data, etag).toCompletableFuture().get();
        } catch (Exception e) {
            assertEquals(e.getCause().getClass(), ConflictingResourceException.class);
        }
    }
}
