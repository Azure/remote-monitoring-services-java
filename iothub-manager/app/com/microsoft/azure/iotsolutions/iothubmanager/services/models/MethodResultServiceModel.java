// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.models;

import com.microsoft.azure.sdk.iot.service.devicetwin.MethodResult;
import play.libs.Json;

import static play.libs.Json.toJson;

public class MethodResultServiceModel {

    private int status;
    private String jsonPayload = null;

    public MethodResultServiceModel() {}

    public MethodResultServiceModel(MethodResult result) {
        this.status = result.getStatus();
        this.jsonPayload = Json.stringify(toJson(result.getPayload()));
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getJsonPayload() {
        return jsonPayload;
    }

    public void setJsonPayload(String jsonPayload) {
        this.jsonPayload = jsonPayload;
    }
}
