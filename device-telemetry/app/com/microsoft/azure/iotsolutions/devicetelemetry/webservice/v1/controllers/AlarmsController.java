// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.controllers;

import org.joda.time.DateTime;
import play.mvc.Result;

import java.util.Date;

import static play.libs.Json.toJson;
import static play.mvc.Results.ok;

public class AlarmsController {
    /**
     * Return a list of alerts. The list of alerts can be paginated, and
     * filtered by device, period of time, status. The list is sorted
     * chronologically, by default starting from the oldest alert, and
     * optionally from the most recent.
     *
     * @return List of alerts.
     */
    public Result list() {
        return ok(toJson("TODO"));
    }

    /**
     * @return One alert.
     */
    public Result get(String id) {
        return ok(toJson("TODO"));
    }

    /**
     * @return One alert.
     */
    public Result patch(String id) {
        return ok(toJson(""));
    }
}
