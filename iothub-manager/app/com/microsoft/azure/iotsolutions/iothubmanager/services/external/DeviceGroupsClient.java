// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.external;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.iothubmanager.services.helpers.HttpRequestHelper;
import com.microsoft.azure.iotsolutions.iothubmanager.services.runtime.IServicesConfig;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;

import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;

public class DeviceGroupsClient implements IDeviceGroupsClient {
    private WSClient wsClient;
    private final String serviceUri;

    @Inject
    public DeviceGroupsClient(WSClient wsClient, IServicesConfig config) {
        this.wsClient = wsClient;
        this.serviceUri = config.getConfigServiceUrl() + "/devicegroups";
    }

    @Override
    public CompletionStage<DeviceGroupApiModel> getDeviceGroupAsync(String deviceGroupId) throws
            CompletionException {
        final WSRequest request = wsClient.url(String.format("%s/%s", this.serviceUri, deviceGroupId));

        return request.get()
                .thenApplyAsync(m -> {
                    try {
                        HttpRequestHelper.checkStatusCode(m, request);
                    } catch (Exception e) {
                        throw new CompletionException("Unable to get device group " + deviceGroupId, e);
                    }
                    return Json.fromJson(m.asJson(), DeviceGroupApiModel.class);
                });
    }
}
