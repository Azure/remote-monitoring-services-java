// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class TwinServiceListModel {

    private final String continuationToken;
    private final List<TwinServiceModel> items;

    public TwinServiceListModel(List<TwinServiceModel> twins, String continuationToken) {
        this.continuationToken = continuationToken;
        this.items = twins;
    }

    @JsonProperty("Items")
    public List<TwinServiceModel> getItems() {
        return this.items;
    }

    public String getContinuationToken() {
        return this.continuationToken;
    }
}
