// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.controllers;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.IAlarmsByRule;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.AlarmApiModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.AlarmListApiModel;
import play.mvc.Result;

import java.util.List;

import static play.libs.Json.toJson;
import static play.mvc.Results.ok;

// TODO: Review and see if we can either extend the Alarm API or the Rules API.
public class AlarmsByRuleController {
    private final IAlarmsByRule alarmsByRule;

    @Inject
    public AlarmsByRuleController(IAlarmsByRule alarmsByRule) {
        this.alarmsByRule = alarmsByRule;
    }

    /**
     * Return a list of alarms grouped by the rule from which the alarm is
     * created. The list can be paginated, and filtered by device, period of
     * time, status. The list is sorted chronologically, by default starting
     * from the oldest alarm, and optionally from the most recent.
     * <p>
     * The list can also contain zero alarms and only a count of occurrences,
     * for instance to know how many alarms are generated for each rule.
     *
     * @return List of alarms.
     */
    public Result list(String from, String to, String order, int skip,
                       int limit, List<String> devices) {
        return ok(toJson(new AlarmListApiModel(this.alarmsByRule.getList())));
    }

    /**
     * @return A list of alarms generated from a specific rule.
     */
    public Result get(String id, String from, String to, String order, int skip,
                      int limit, List<String> devices) {
        return ok(toJson(new AlarmApiModel(this.alarmsByRule.get(id))));
    }
}
