// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external;

import java.util.List;

public class SimulationApiModel {

    private List<DeviceModelRef> deviceModels;
    private String etag;
    private String id;

    public SimulationApiModel() {
    }

    public SimulationApiModel(List<DeviceModelRef> deviceModels, String etag, String id) {
        this.deviceModels = deviceModels;
        this.etag = etag;
        this.id = id;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<DeviceModelRef> getDeviceModels() {
        return deviceModels;
    }

    public void setDeviceModels(List<DeviceModelRef> deviceModels) {
        this.deviceModels = deviceModels;
    }
}
