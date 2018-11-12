package com.microsoft.azure.iotsolutions.iothubmanager.services.helpers;

import com.google.inject.Inject;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;

import java.time.Duration;

public class WsRequestBuilder {
    private WSClient ws;

    @Inject
    public WsRequestBuilder(WSClient ws) {
        this.ws = ws;
    }

    public WSRequest prepareRequest(String url) {
        WSRequest wsRequest = this.ws
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Referer", "IotHubManager");
        wsRequest.setRequestTimeout(Duration.ofSeconds(10));
        return wsRequest;
    }
}
