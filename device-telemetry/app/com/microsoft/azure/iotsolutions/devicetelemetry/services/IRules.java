// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.AlarmCountByRuleServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.RuleServiceModel;
import org.joda.time.DateTime;

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

    CompletionStage<List<AlarmCountByRuleServiceModel>> getAlarmCountForList(
        DateTime from,
        DateTime to,
        String order,
        int skip,
        int limit,
        String[] devices
    ) throws Exception;

    CompletionStage<RuleServiceModel> postAsync(
        RuleServiceModel ruleServiceModel);

    CompletionStage<RuleServiceModel> putAsync(
        RuleServiceModel ruleServiceModel);

    CompletionStage deleteAsync(String id);
}
