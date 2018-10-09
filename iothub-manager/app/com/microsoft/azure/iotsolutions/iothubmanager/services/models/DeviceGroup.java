// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.models;

public class DeviceGroup {
    private final String id;
    private final String name;
    private final String query;

    public DeviceGroup(String id, String name, String query) {
        this.id = id;
        this.name = name;
        this.query = query;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getQuery() {
        return this.query;
    }
}
