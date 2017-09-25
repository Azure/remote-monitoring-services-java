// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.controllers;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.IAlarms;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.controllers.helpers.DateHelper;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.AlarmApiModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.AlarmListApiModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.AlarmStatus;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import static play.libs.Json.toJson;

public class AlarmsController extends Controller {
    private static final Logger.ALogger log = Logger.of(AlarmsController.class);

    private final IAlarms alarms;

    @Inject
    public AlarmsController(IAlarms alarms) {
        this.alarms = alarms;
    }

    /**
     * Return a list of alerts. The list of alerts can be paginated, and
     * filtered by device, period of time, status. The list is sorted
     * chronologically, by default starting from the oldest alert, and
     * optionally from the most recent.
     *
     * @return List of alerts.
     */
    public Result list(String from, String to, String order, int skip,
                       int limit, String devices) throws Exception {
        // TODO: move this logic to the storage engine, depending on the
        // storage type the limit will be different. 200 is DocumentDb
        // limit for the IN clause.
        String[] deviceIds = new String[0];
        if (devices != null) {
            deviceIds = devices.split(",");
        }
        if (deviceIds.length > 200) {
            log.warn("The client requested too many devices: {}", deviceIds.length);
            return badRequest("The number of devices cannot exceed 200");
        }

        return ok(toJson(new AlarmListApiModel(this.alarms.getList(DateHelper.parseDate(from), DateHelper.parseDate(to), order, skip, limit, deviceIds))));
    }

    /**
     * @return One alert.
     */
    public Result get(String id) throws Exception {
        return ok(toJson(new AlarmApiModel(this.alarms.get(id))));
    }

    /**
     * @return One alert.
     */
    public Result patch(String id) throws Exception {

        AlarmStatus alarm = Json.fromJson(request().body().asJson(), AlarmStatus.class);

        // validate input
        if (!(alarm.status.equalsIgnoreCase("open") ||
              alarm.status.equalsIgnoreCase("closed") ||
              alarm.status.equalsIgnoreCase("acknowledged"))) {

            return badRequest(
                "Status must be `open`, acknowledged`, or `closed`. " +
                    "Value provided: " + alarm.status);
        }

        return ok(toJson(new AlarmApiModel(this.alarms.update(id, alarm.status))));
    }
}
