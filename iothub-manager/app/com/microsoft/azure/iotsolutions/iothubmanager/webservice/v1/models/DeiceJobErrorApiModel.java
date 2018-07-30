// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceJobErrorServiceModel;

public class DeiceJobErrorApiModel {

    private String code;
    private String description;

    public DeiceJobErrorApiModel()
    {

    }

    public DeiceJobErrorApiModel(DeviceJobErrorServiceModel error){
        this.code = error.getCode();
        this.description = error.getDescription();
    }

    @JsonProperty("Code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @JsonProperty("Description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
