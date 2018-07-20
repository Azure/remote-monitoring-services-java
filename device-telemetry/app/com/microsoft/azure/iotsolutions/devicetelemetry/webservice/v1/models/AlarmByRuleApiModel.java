// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.AlarmServiceModel;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class AlarmByRuleApiModel {
    private int count;
    private String status;
    private DateTime created;
    private AlarmRuleApiModel rule;

    private DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");

    public AlarmByRuleApiModel(
        final int count,
        final String status,
        final DateTime created,
        AlarmRuleApiModel rule) {
        this.count = count;
        this.status = status;
        this.created = created;
        this.rule = rule;
    }

    public AlarmByRuleApiModel(
        final int count,
        final String status,
        final DateTime created,
        final AlarmServiceModel alarm) {
        this.count = count;
        this.status = status;
        this.created = created;
        this.rule = new AlarmRuleApiModel(alarm.getRuleId(), alarm.getRuleSeverity(), alarm.getRuleDescription());
    }

    @JsonProperty("Rule")
    public AlarmRuleApiModel getRule() { return this.rule; }

    @JsonProperty("Count")
    public int getCount() { return  this.count; }

    public void setCount(int count) { this.count = count; }

    @JsonProperty("Created")
    public String getCreated() {
        if(this.created == null) {
            return null;
        }

        return dateFormat.print(this.created.toDateTime(DateTimeZone.UTC));
    }

    public void setCreated(DateTime created) { this.created = created; }

    @JsonProperty("Status")
    public String getStatus() { return  String.valueOf(this.status); }

    public void setStatus(String status) { this.status = status; }
}
