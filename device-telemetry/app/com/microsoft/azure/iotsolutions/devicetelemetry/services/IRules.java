// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.RuleServiceModel;

import java.util.ArrayList;

@ImplementedBy(Rules.class)
public interface IRules {
    RuleServiceModel get(String id);

    ArrayList<RuleServiceModel> getList();

    RuleServiceModel post(RuleServiceModel ruleServiceModel);

    RuleServiceModel put(RuleServiceModel ruleServiceModel);

    void delete(String id);
}
