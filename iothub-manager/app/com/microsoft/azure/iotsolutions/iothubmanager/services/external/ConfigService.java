// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.external;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.iothubmanager.services.helpers.HashMapHelper;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceTwinProperties;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceTwinServiceModel;
import com.microsoft.azure.iotsolutions.iothubmanager.services.runtime.IServicesConfig;
import play.Logger;
import play.libs.Json;
import play.libs.ws.*;

import java.util.concurrent.*;

public class ConfigService implements IConfigService {

    private static final Logger.ALogger log = Logger.of(ConfigService.class);

    private final WSClient wsClient;
    private final String serviceUrl;

    @Inject
    public ConfigService(final IServicesConfig config, final WSClient wsClient) {
        this.serviceUrl = config.getConfigServiceUrl();
        this.wsClient = wsClient;
    }

    @Override
    public CompletionStage updateDeviceGroupFiltersAsync(DeviceTwinServiceModel twin) {
        DeviceGroupFiltersApiModel model = new DeviceGroupFiltersApiModel();

        if (twin.getTags() != null) {
            model.setTags(HashMapHelper.mapToHashSet("", twin.getTags()));
        }

        DeviceTwinProperties properties = twin.getProperties();
        if (properties != null && properties.getReported() != null) {
            model.setReported(HashMapHelper.mapToHashSet("", properties.getReported()));
        }

        String url = this.serviceUrl + "/devicegroupfilters";
        return CompletableFuture.runAsync(() ->
            this.wsClient.url(url)
                .post(Json.toJson(model))
                .handle((response, error) -> {
                    if (error != null) {
                        String message = String.format("Fail to access config service: %s", url);
                        log.error(message, error.getCause());
                    }
                    return response;
                })
        );
    }
}
