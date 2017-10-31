// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.models;

import org.joda.time.DateTime;

public final class AlarmCountByRuleServiceModel {
    private final int count;
    private final String status;
    private final DateTime messageTime;
    private final RuleServiceModel rule;

    public AlarmCountByRuleServiceModel(
        final int count,
        final String status,
        final DateTime messageTime,
        final RuleServiceModel rule) {

        this.count = count;
        this.status = status;
        this.messageTime = messageTime;
        this.rule = rule;
    }

    public int getCount() {
        return count;
    }

    public String getStatus() {
        return this.status;
    }

    public DateTime getMessageTime() {
        return this.messageTime;
    }

    public RuleServiceModel getRule() {
        return this.rule;
    }

}
