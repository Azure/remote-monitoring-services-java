// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.controllers;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.IMessages;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.controllers.helpers.DateHelper;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.MessageListApiModel;
import org.joda.time.DateTime;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import static play.libs.Json.toJson;

/**
 * Gets telemetry, rules, and alarm messages
 */
public final class MessagesController extends Controller {

    private static final Logger.ALogger log = Logger.of(MessagesController.class);

    private final IMessages messages;

    @Inject
    public MessagesController(final IMessages messages) {
        this.messages = messages;
    }

    public Result list(
        String from,
        String to,
        String order,
        Integer skip,
        Integer limit,
        String devices) throws InvalidInputException {

        DateTime fromDate = DateHelper.parseDate(from);
        DateTime toDate = DateHelper.parseDate(to);

        if (order == null) order = "asc";
        if (skip == null) skip = 0;
        if (limit == null) limit = 1000;

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

        return ok(toJson(new MessageListApiModel(this.messages.getList(
            fromDate,
            toDate,
            order,
            skip,
            limit,
            deviceIds))));
    }
}
