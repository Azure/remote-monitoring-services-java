// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models;

import com.microsoft.azure.iotsolutions.iothubmanager.services.models.MethodResultServiceModel;

public class MethodResultApiModel extends MethodResultServiceModel {

    public MethodResultApiModel(MethodResultServiceModel model) {
        this.setStatus(model.getStatus());
        this.setJsonPayload(model.getJsonPayload());
    }
}
