// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.MessageServiceModel;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Public model used by the web service.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class MessageApiModel {

    private String deviceId = null;
    private DateTime time = null;
    private String body = null;

    private DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");

    /**
     * Create an instance given the property values.
     *
     * @param deviceId
     * @param time
     * @param body
     */
    public MessageApiModel(
        final String deviceId,
        final DateTime time,
        final String body) {

        this.deviceId = deviceId;
        this.time = time;
        this.body = body;
    }

    /**
     * Create instance given a service model.
     *
     * @param message service model
     */
    public MessageApiModel(final MessageServiceModel message) {
        this.deviceId = message.getDeviceId();
        this.time = message.getTime();
        this.body = message.getBody();
    }

    @JsonProperty("DeviceId")
    public String getDeviceId() {
        return this.deviceId;
    }

    @JsonProperty("Time")
    public String getTime() {
        return this.dateFormat.print(this.time.toDateTime(DateTimeZone.UTC));
    }

    @JsonProperty("Body")
    public String getBody() {
        return this.body;
    }
}
