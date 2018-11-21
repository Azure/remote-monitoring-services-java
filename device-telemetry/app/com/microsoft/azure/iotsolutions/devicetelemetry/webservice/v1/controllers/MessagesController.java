// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.controllers;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.IMessages;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidConfigurationException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.TimeSeriesParseException;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.auth.Authorize;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.controllers.helpers.DateHelper;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.MessageListApiModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.QueryApiModel;
import org.joda.time.DateTime;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import static play.libs.Json.fromJson;
import static play.libs.Json.toJson;

/**
 * Gets telemetry, rules, and alarm messages
 */
public final class MessagesController extends Controller {

    private static final Logger.ALogger log = Logger.of(MessagesController.class);

    private final IMessages messages;

    private static final int DEVICE_LIMIT = 1000;

    @Inject
    public MessagesController(final IMessages messages) {
        this.messages = messages;
    }

    @Authorize("ReadAll")
    public Result list(
            String from,
            String to,
            String order,
            Integer skip,
            Integer limit,
            String devices) throws
            InvalidInputException,
            InvalidConfigurationException,
            TimeSeriesParseException {

        DateTime fromDate = DateHelper.parseDate(from);
        DateTime toDate = DateHelper.parseDate(to);

        String[] deviceIds = new String[0];
        if (devices != null) {
            deviceIds = devices.split(",");
        }

        return this.listMessagesHelper(fromDate, toDate, order, skip, limit, deviceIds);
    }

    @Authorize("ReadAll")
    public Result post() throws InvalidConfigurationException, InvalidInputException, TimeSeriesParseException {

        QueryApiModel body = fromJson(request().body().asJson(), QueryApiModel.class);
        String[] deviceIds = body.getDevices() == null
                ? new String[0]
                : body.getDevices().toArray(new String[body.getDevices().size()]);

        DateTime fromDate = DateHelper.parseDate(body.getFrom());
        DateTime toDate = DateHelper.parseDate(body.getTo());

        return this.listMessagesHelper(
                fromDate,
                toDate,
                body.getOrder(),
                body.getSkip(),
                body.getLimit(),
                deviceIds);
    }

    private Result listMessagesHelper(
            DateTime from,
            DateTime to,
            String order,
            Integer skip,
            Integer limit,
            String[] deviceIds) throws
            InvalidConfigurationException,
            InvalidInputException,
            TimeSeriesParseException {

        // TODO: move this logic to the storage engine, depending on the
        // storage type the limit will be different. DEVICE_LIMIT is CosmosDb
        // limit for the IN clause.
        if (deviceIds.length > DEVICE_LIMIT) {
            log.warn("The client requested too many devices: {}", deviceIds.length);
            return badRequest("The number of devices cannot exceed " + DEVICE_LIMIT);
        }

        return ok(toJson(new MessageListApiModel(this.messages.getList(
                from,
                to,
                order,
                skip,
                limit,
                deviceIds))));
    }
}
