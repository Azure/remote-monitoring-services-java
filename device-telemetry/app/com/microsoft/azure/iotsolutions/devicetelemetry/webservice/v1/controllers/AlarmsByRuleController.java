// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.controllers;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.IAlarms;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.IRules;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.auth.Authorize;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.controllers.helpers.DateHelper;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.AlarmByRuleListApiModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.AlarmListByRuleApiModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.QueryApiModel;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static play.libs.Json.fromJson;
import static play.libs.Json.toJson;

// TODO: Review and see if we can either extend the Alarm API or the Rules API.
public class AlarmsByRuleController extends Controller {
    private static final Logger.ALogger log = Logger.of(AlarmsByRuleController.class);

    private final IAlarms alarmsService;
    private final IRules rulesService;

    private static final int DEVICE_LIMIT = 1000;

    @Inject
    public AlarmsByRuleController(IAlarms alarmsService, IRules rulesService) {
        this.alarmsService = alarmsService;
        this.rulesService = rulesService;
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
    @Authorize("ReadAll")
    public CompletionStage<Result> getAsync(String from, String to, String order, int skip,
                                             int limit, String devices) throws Exception {
        String[] deviceIds = new String[0];
        if (devices != null) {
            deviceIds = devices.split(",");
        }

        return this.getAlarmCountByRuleHelper(from, to, order, skip, limit, deviceIds);
    }

    /**
     * Return a list of alarms grouped by the rule from which the alarm is
     * created. The list can be paginated, and filtered by device, period of
     * time, status. The list is sorted chronologically, by default starting
     * from the oldest alarm, and optionally from the most recent. Query parameters
     * are in the body of the request, in the format of QueryApiModel
     * <p>
     * The list can also contain zero alarms and only a count of occurrences,
     * for instance to know how many alarms are generated for each rule.
     *
     * @return List of alarms.
     */
    @Authorize("ReadAll")
    public CompletionStage<Result> postAsync() throws Exception {
        QueryApiModel body = fromJson(request().body().asJson(), QueryApiModel.class);
        String[] deviceIds = body.getDevices() == null
                ? new String[0]
                : body.getDevices().toArray(new String[body.getDevices().size()]);

        return this.getAlarmCountByRuleHelper(
                body.getFrom(),
                body.getTo(),
                body.getOrder(),
                body.getSkip(),
                body.getLimit(),
                deviceIds);
    }

    /**
     * @return A list of alarms generated from a specific rule. May be filtered
     * based on query parameters in body of request.
     */
    @Authorize("ReadAll")
    public Result post(String id) throws Exception {
        QueryApiModel body = fromJson(request().body().asJson(), QueryApiModel.class);
        String[] deviceIds = body.getDevices() == null
                ? new String[0]
                : body.getDevices().toArray(new String[body.getDevices().size()]);

        return this.getAlarmListByRuleHelper(
                id,
                body.getFrom(),
                body.getTo(),
                body.getOrder(),
                body.getSkip(),
                body.getLimit(),
                deviceIds);
    }

    /**
     * @return A list of alarms generated from a specific rule.
     */
    @Authorize("ReadAll")
    public Result get(String id, String from, String to, String order, int skip,
                      int limit, String devices) throws Exception {
        String[] deviceIds = new String[0];
        if (devices != null) {
            deviceIds = devices.split(",");
        }

        return this.getAlarmListByRuleHelper(id, from, to, order, skip, limit, deviceIds);
    }

    private CompletionStage<Result> getAlarmCountByRuleHelper(String from, String to, String order, int skip,
                                            int limit, String[] deviceIds) throws Exception {
        // TODO: move this logic to the storage engine, depending on the
        // storage type the limit will be different. DEVICE_LIMIT is CosmosDb
        // limit for the IN clause.
        if (deviceIds.length > DEVICE_LIMIT) {
            log.warn("The client requested too many devices: {}", deviceIds.length);
            return CompletableFuture.completedFuture(
                    badRequest("The number of devices cannot exceed " + DEVICE_LIMIT));
        }

        return this.rulesService.getAlarmCountForList(
                DateHelper.parseDate(from),
                DateHelper.parseDate(to),
                order,
                skip,
                limit,
                deviceIds)
                .thenApply(alarmByRuleList -> ok(toJson(
                        new AlarmByRuleListApiModel(alarmByRuleList))));
    }

    private Result getAlarmListByRuleHelper(String id, String from, String to, String order, int skip,
                                            int limit, String[] deviceIds) throws Exception {
        // TODO: move this logic to the storage engine, depending on the
        // storage type the limit will be different. DEVICE_LIMIT is CosmosDb
        // limit for the IN clause.
        if (deviceIds.length > DEVICE_LIMIT) {
            log.warn("The client requested too many devices: {}", deviceIds.length);
            return badRequest("The number of devices cannot exceed DEVICE_LIMIT");
        }

        return ok(toJson(new AlarmListByRuleApiModel(
                this.alarmsService.getListByRuleId(
                        id,
                        DateHelper.parseDate(from),
                        DateHelper.parseDate(to),
                        order,
                        skip,
                        limit,
                        deviceIds))));
    }
}
