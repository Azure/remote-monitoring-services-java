// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services;

import com.microsoft.azure.iotsolutions.uiconfig.services.external.SimulationServiceClient;
import com.microsoft.azure.iotsolutions.uiconfig.services.http.HttpRequest;
import com.microsoft.azure.iotsolutions.uiconfig.services.http.HttpResponse;
import com.microsoft.azure.iotsolutions.uiconfig.services.http.IHttpClient;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.ServicesConfig;
import helpers.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

public class SimulationServiceClientTest {
    private String MockServiceUri = "http://mockhubManager";
    private IHttpClient mockHttpClient;
    private SimulationServiceClient client;
    private String content = "{" +
            "  \"Items\": [" +
            "    {" +
            "      \"key\": \"value\"," +
            "      \"Properties\": {" +
            "        \"Type\": \"Truck\"," +
            "        \"Location\": \"Field\"," +
            "        \"address\": {" +
            "          \"street\": \"ssss\"," +
            "          \"NO\": \"1111\"" +
            "        }," +
            "        \"Latitude\": 47.445301," +
            "        \"Longitude\": -122.296307" +
            "      }" +
            "    }," +
            "    {" +
            "      \"Properties\": {" +
            "        \"Type1\": \"Truck\"," +
            "        \"Location\": \"Field\"," +
            "        \"address1\": {" +
            "          \"street\": \"ssss\"," +
            "          \"NO\": \"1111\"" +
            "        }," +
            "        \"Latitude1\": 47.445301," +
            "        \"Longitude\": -122.296307" +
            "      }" +
            "    }" +
            "  ]" +
            "}";

    @Before
    public void setUp() throws IOException {
        mockHttpClient = Mockito.mock(IHttpClient.class);
    }

    @Test(timeout = 100000)
    @Category({UnitTest.class})
    public void getDeviceTwinNamesAsyncTest() throws URISyntaxException, ExecutionException, InterruptedException {
        HttpResponse response = new HttpResponse(200, null, content);
        Mockito.when(mockHttpClient.getAsync(Mockito.any(HttpRequest.class)))
                .thenReturn(CompletableFuture.supplyAsync(() -> response));
        client = new SimulationServiceClient(
                mockHttpClient,
                new ServicesConfig(null,null, MockServiceUri, null, 0, 0,null,null,null));
        HashSet<String> result = this.client.getDevicePropertyNamesAsync().toCompletableFuture().get();
        TreeSet<String> treeset = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        treeset.addAll(result);
        assertEquals(String.join(",", treeset), "address.NO,address.street,address1.NO,address1.street,Latitude,Latitude1,Location,Longitude,Type,Type1");
    }
}
