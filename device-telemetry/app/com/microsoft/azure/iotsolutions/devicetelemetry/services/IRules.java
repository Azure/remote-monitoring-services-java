// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidConfigurationException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.ResourceNotFoundException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.RuleServiceModel;

import java.util.List;
import java.util.concurrent.CompletionStage;

@ImplementedBy(Rules.class)
public interface IRules {
    CompletionStage<RuleServiceModel> getAsync(String id);

    CompletionStage<List<RuleServiceModel>> getListAsync(
        String order,
        int skip,
        int limit,
        String groupId);

    CompletionStage<RuleServiceModel> postAsync(
        RuleServiceModel ruleServiceModel);

    void createFromTemplate(String template) throws
        InvalidConfigurationException,
        ResourceNotFoundException;

    CompletionStage<RuleServiceModel> putAsync(
        RuleServiceModel ruleServiceModel);

    CompletionStage deleteAsync(String id);
}
