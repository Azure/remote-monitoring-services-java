// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.ConditionServiceModel;

import java.util.ArrayList;

public class ConditionListApiModel {
    private final ArrayList<ConditionApiModel> conditions;

    public ConditionListApiModel(final ArrayList<ConditionServiceModel> conditions) {
        this.conditions = new ArrayList<>();
        if (conditions != null) {
            for (ConditionServiceModel condition : conditions) {
                this.conditions.add(new ConditionApiModel(condition));
            }
        }
    }

    @JsonProperty("Conditions")
    public ArrayList<ConditionApiModel> getConditions() {
        return this.conditions;
    }
}
