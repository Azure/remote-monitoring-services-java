// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.controllers;

import play.mvc.Result;

import static play.libs.Json.toJson;
import static play.mvc.Results.ok;

// TODO: Review and see if we can either extend the Alert API or the Rules API.
public class AlertsByRuleController {
    /**
     * Return a list of alerts grouped by the rule from which the alert is
     * created. The list can be paginated, and filtered by device, period of
     * time, status. The list is sorted chronologically, by default starting
     * from the oldest alert, and optionally from the most recent.
     *
     * The list can also contain zero alerts and only a count of occurrences,
     * for instance to know how many alerts are generated for each rule.
     *
     * @return List of alerts.
     */
    public Result list() {
        return ok(toJson("TODO"));
    }

    /**
     * @return A list of alerts generated from a specific rule.
     */
    public Result get(String id) {
        return ok(toJson("TODO"));
    }
}
