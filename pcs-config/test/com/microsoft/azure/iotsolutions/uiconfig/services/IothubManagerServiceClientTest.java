// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services;

import com.microsoft.azure.iotsolutions.uiconfig.services.external.IothubManagerServiceClient;
import com.microsoft.azure.iotsolutions.uiconfig.services.http.HttpRequest;
import com.microsoft.azure.iotsolutions.uiconfig.services.http.HttpResponse;
import com.microsoft.azure.iotsolutions.uiconfig.services.http.IHttpClient;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.DeviceTwinName;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.ServicesConfig;
import helpers.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import java.io.*;
import java.net.URISyntaxException;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

public class IothubManagerServiceClientTest {

    private String MockServiceUri = "http://mockhubManager";
    private IHttpClient mockHttpClient;
    private IothubManagerServiceClient client;
    private String content = "{" +
            "  \"Items\": [" +
            "    {" +
            "      \"Properties\": {" +
            "        \"Reported\": {" +
            "          \"device1a\": \"a\"," +
            "          \"device1b\": {" +
            "            \"b\": \"b\"" +
            "          }," +
            "          \"c\": \"c\"" +
            "        }" +
            "      }," +
            "      \"Tags\": {" +
            "        \"device1e\": \"e\"," +
            "        \"device1f\": {" +
            "          \"f\": \"f\"" +
            "        }," +
            "        \"g\": \"g\"" +
            "      }" +
            "    }," +
            "    {" +
            "      \"Properties\": {" +
            "        \"Reported\": {" +
            "          \"device2a\": \"a\"," +
            "          \"device2b\": {" +
            "            \"b\": \"b\"" +
            "          }," +
            "          \"c\": \"c\"" +
            "        }" +
            "      }," +
            "      \"Tags\": {" +
            "        \"device2e\": \"e\"," +
            "        \"device2f\": {" +
            "          \"f\": \"f\"" +
            "        }," +
            "        \"g\": \"g\"" +
            "      }" +
            "    }" +
            "  ]," +
            "  \"$metadata\": {" +
            "    \"$type\": \"DeviceList;1\"," +
            "    \"$uri\": \"/v1/devices\"" +
            "  }" +
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
        client = new IothubManagerServiceClient(
                mockHttpClient,
                new ServicesConfig(null,null, null, MockServiceUri, 0, 0, null,null,null));
        DeviceTwinName result = this.client.getDeviceTwinNamesAsync().toCompletableFuture().get();
        assertEquals(String.join(",", new TreeSet<String>(result.getTags())), "device1e,device1f.f,device2e,device2f.f,g");
        assertEquals(String.join(",", new TreeSet<String>(result.getReportedProperties())), "c,device1a,device1b.b,device2a,device2b.b");
    }
}
