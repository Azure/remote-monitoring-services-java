// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.*;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.Version;

import java.util.*;

public final class TwinPropertiesListApiModel {

    private final List<TwinPropertiesApiModel> items;
    private String continuationToken;

    public TwinPropertiesListApiModel(final TwinServiceListModel twins) {
        this.items = new LinkedList<>();
        this.continuationToken = twins.getContinuationToken();
        for (TwinServiceModel t : twins.getItems()) {
            this.items.add(new TwinPropertiesApiModel(t.getDeviceId(), t.getModuleId(), t.getProperties()));
        }
    }

    @JsonProperty("Items")
    public List<TwinPropertiesApiModel> getItems() {
        return this.items;
    }

    @JsonProperty("$metadata")
    public Dictionary<String, String> getMetadata() {
        return new Hashtable<String, String>() {{
            put("$type", "TwinPropertiesList;" + Version.NUMBER);
            put("$uri", "/" + Version.PATH + "/devices");
        }};
    }

    @JsonProperty("ContinuationToken")
    public String getContinuationToken() {
        return continuationToken;
    }

    public void setContinuationToken(String continuationToken) {
        this.continuationToken = continuationToken;
    }
}
