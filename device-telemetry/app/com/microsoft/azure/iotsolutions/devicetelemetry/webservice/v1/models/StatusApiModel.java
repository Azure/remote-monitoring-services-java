// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.StatusTuple;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.runtime.Uptime;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.Version;
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

    public StatusApiModel(StatusTuple statusInfo) {
        this.status = statusInfo.isHealthy() ? "OK" : "ERROR";
        if (!statusInfo.getStatusMessage().isEmpty()) {
            this.status += ":" + statusInfo.getStatusMessage();
        }
    }

    @JsonProperty("Name")
    public String getName() {
        return "devicetelemetry";
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

    @JsonProperty("UID")
    public String getUID() {
        return Uptime.getProcessId();
    }

    @JsonProperty("Properties")
    public Dictionary<String, String> getProperties() {
        return new Hashtable<String, String>() {{
            put("Simulation", "on");
            put("Region", "US");
            put("DebugMode", "off");
        }};
    }

    @JsonProperty("Dependencies")
    public Dictionary<String, String> getDependencies() {
        return new Hashtable<String, String>() {{
            put("Storage", status);
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
