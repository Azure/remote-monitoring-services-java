// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.models;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

public final class MessageServiceModel {
    private final String deviceId;
    private final DateTime time;
    private final Object body;

    public MessageServiceModel(
        final String deviceId,
        final String time,
        final Object body) {

        this.deviceId = deviceId;
        this.time = DateTime.parse(time, ISODateTimeFormat.dateTimeParser().withZoneUTC());
        this.body = body;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public Object getBody() {
        return this.body;
    }

    public DateTime getTime() {
        return this.time;
    }
}
