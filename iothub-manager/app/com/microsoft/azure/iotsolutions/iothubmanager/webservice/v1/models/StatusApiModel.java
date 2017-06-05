// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.runtime.Uptime;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.Version;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Dictionary;
import java.util.Hashtable;

@JsonPropertyOrder({"Status", "CurrentTime", "StartTime", "UpTime", "Properties", "Dependencies", "$metadata"})
public final class StatusApiModel {

    private String status;
    private DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");

    public StatusApiModel(final Boolean isOk, final String msg) {
        this.status = isOk ? "OK" : "ERROR";
        if (!msg.isEmpty()) {
            this.status += ":" + msg;
        }
    }

    @JsonProperty("Status")
    public String getStatus() {
        return this.status;
    }

    @JsonProperty("CurrentTime")
    public String getCurrentTime() {
        return dateFormat.print(DateTime.now().toDateTime(DateTimeZone.UTC));
    }

    @JsonProperty("StartTime")
    public String getStartTime() {
        return dateFormat.print(Uptime.getStart());
    }

    @JsonProperty("UpTime")
    public long getUpTime() {
        return Uptime.getDuration().getMillis() / 1000;
    }

    @JsonProperty("Properties")
    public Dictionary<String, String> getProperties() {
        return new Hashtable<String, String>() {{
            put("Foo", "Bar");
            put("Simulation", "on");
            put("Region", "US");
            put("DebugMode", "off");
        }};
    }

    @JsonProperty("Dependencies")
    public Dictionary<String, String> getDependencies() {
        return new Hashtable<String, String>() {{
            put("IoTHub", "OK:...msg...");
            put("Storage", "ERROR:timeout after 3 secs");
            put("Auth", "ERROR:certificate expired");
        }};
    }

    @JsonProperty("$metadata")
    public Dictionary<String, String> getMetadata() {
        return new Hashtable<String, String>() {{
            put("$type", "Status;" + Version.NAME);
            put("$uri", "/" + Version.NAME + "/status");
        }};
    }
}
