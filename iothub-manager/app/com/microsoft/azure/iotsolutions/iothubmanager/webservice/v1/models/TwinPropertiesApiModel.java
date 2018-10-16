// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.TwinProperties;

import java.util.HashMap;

public class TwinPropertiesApiModel {
    private final String deviceId;
    private final String moduleId;
    private final TwinProperties properties;

    public TwinPropertiesApiModel(final String deviceId, final String moduleId, final TwinProperties properties) {
        this.deviceId = deviceId;
        this.moduleId = moduleId;
        this.properties = properties;
    }

    @JsonProperty("DeviceId")
    public String getDeviceId() {
        return deviceId;
    }

    @JsonProperty("ModuleId")
    public String getModuleId() {
        return moduleId;
    }

    @JsonProperty("Desired")
    public HashMap<String, Object> getDesiredProperties() {
        return this.properties != null ? this.properties.getDesired() : null;
    }

    @JsonProperty("Reported")
    public HashMap<String, Object> getReportedProperties() {
        return this.properties != null ? this.properties.getReported() : null;
    }
}
