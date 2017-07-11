// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.controllers;

import play.mvc.Result;

import static play.libs.Json.toJson;
import static play.mvc.Results.ok;

public class RulesController {
    /**
     * Return a list of rules. The list of rules can be paginated, and
     * filtered by device and status. The list is sorted
     * chronologically, by default starting from the oldest alert, and
     * optionally from the most recent.
     *
     * @return List of rules.
     */
    public Result list() {
        return ok(toJson("TODO"));
    }

    /**
     * @return One rule.
     */
    public Result get(String id) {
        return ok(toJson("TODO"));
    }

    /**
     * @return new rule.
     */
    public Result post() { return ok(toJson("TODO")); }

    /**
     * @return updated rule.
     */
    public Result put(String id) { return ok(toJson("TODO")); }

    /**
     * @return success message.
     */
    public Result delete(String id) {
        return ok(toJson("TODO"));
    }
}
