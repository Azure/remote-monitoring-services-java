// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.controllers;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.IMessages;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.MessageListApiModel;
import play.mvc.Controller;
import play.mvc.Result;

import static play.libs.Json.toJson;

/**
 * Gets device telemetry, rules, and alert messages
 */
public final class MessagesController extends Controller {

    private final IMessages messages;

    @Inject
    public MessagesController(final IMessages messages) {
        this.messages = messages;
    }

    public Result list() {
        return ok(toJson(new MessageListApiModel(this.messages.getList())));
    }
}
