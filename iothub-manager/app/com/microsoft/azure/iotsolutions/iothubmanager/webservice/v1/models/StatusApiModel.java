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

@JsonPropertyOrder({"Name", "Status", "CurrentTime", "StartTime", "UpTime", "UID", "Properties", "Dependencies", "$metadata"})
public final class StatusApiModel {

    private String name = "IoTHubManager";
    private String status;
    private String uid = Uptime.getProcessId();
    private DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");
    private Dictionary<String, String> dependencies;

    public StatusApiModel(final Boolean isOk, final String msg) {
        this.status = isOk ? "OK" : "ERROR";
        if (!msg.isEmpty()) {
            this.status += ":" + msg;
        }
        this.dependencies = new Hashtable<String, String>() {{
            put("IoTHub", "OK:...msg...");
        }};
    }

    @JsonProperty("Name")
    @JsonPropertyOrder()
    public String getName() {
        return this.name;
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
        return this.uid;
    }

    @JsonProperty("Properties")
    public Dictionary<String, String> getProperties() {
        return new Hashtable<String, String>() {{
            put("Foo", "Bar");
        }};
    }

    @JsonProperty("Dependencies")
    public Dictionary<String, String> getDependencies() {
        return this.dependencies;
    }

    @JsonProperty("$metadata")
    public Dictionary<String, String> getMetadata() {
        return new Hashtable<String, String>() {{
            put("$type", "Status;" + Version.NUMBER);
            put("$uri", "/" + Version.PATH + "/status");
        }};
    }
}
