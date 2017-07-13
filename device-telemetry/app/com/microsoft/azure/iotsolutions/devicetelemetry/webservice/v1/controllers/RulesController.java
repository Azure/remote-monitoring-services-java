// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.controllers;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.IRules;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.RuleServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.RuleApiModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.RuleListApiModel;
import play.mvc.Result;

import static play.libs.Json.toJson;
import static play.mvc.Results.ok;

public class RulesController {

    private final IRules rules;

    @Inject
    public RulesController(final IRules rules) {
        this.rules = rules;
    }

    /**
     * Return a list of rules. The list of rules can be paginated, and
     * filtered by device and status. The list is sorted
     * chronologically, by default starting from the oldest alert, and
     * optionally from the most recent.
     *
     * @return List of rules
     */
    public Result list() {
        return ok(toJson(new RuleListApiModel(this.rules.getList())));
    }

    /**
     * @return One rule
     */
    public Result get(String id) {
        return ok(toJson(new RuleApiModel(this.rules.get(id))));
    }

    /**
     * Returns a newly created rule if id is NULL, or modifies existing
     * rule with specified id.
     *
     * @return newly created rule
     */
    public Result post() {
        return ok(toJson(new RuleApiModel(this.rules.post(new RuleServiceModel()))));
    }

    /**
     * Modify existing rule with specified id.
     *
     * @return updated rule
     */
    public Result put(String id) {
        return ok(toJson(new RuleApiModel(this.rules.put(new RuleServiceModel()))));
    }

    /**
     * Delete rule with specified id
     *
     * @return OK
     */
    public Result delete(String id) {

        return ok();
    }
}
