// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.models;

public class AlarmRuleServiceModel {
    private final String id;
    private final String description;

    public AlarmRuleServiceModel() {
        this.id = null;
        this.description = null;
    }

    public  AlarmRuleServiceModel(
        final String id,
        final String description
    ) {
        this.id = id;
        this.description = description;
    }

    public String getId() { return  this.id; }

    public  String getDescription() { return this.description; }
}
