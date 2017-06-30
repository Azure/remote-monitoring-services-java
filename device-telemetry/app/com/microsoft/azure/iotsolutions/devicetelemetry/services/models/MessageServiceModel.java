// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.models;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

public final class MessageServiceModel {
    private final String deviceId;
    private final DateTime time;
    private final String body;

    public MessageServiceModel (
        final String deviceId,
        final String time,
        final String body) {

        this.deviceId = deviceId;
        this.time = DateTime.parse(time, ISODateTimeFormat.dateTimeParser().withZoneUTC());
        this.body = body;
    }

    public String getDeviceId() { return deviceId; }

    public String getBody() { return body; }

    public DateTime getTime() { return time; }
}
