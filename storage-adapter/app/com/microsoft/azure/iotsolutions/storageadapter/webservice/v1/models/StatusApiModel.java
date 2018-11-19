// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.microsoft.azure.iotsolutions.storageadapter.services.models.StatusServiceModel;
import com.microsoft.azure.iotsolutions.storageadapter.webservice.runtime.Uptime;
import com.microsoft.azure.iotsolutions.storageadapter.webservice.v1.Version;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Dictionary;
import java.util.Hashtable;

@JsonPropertyOrder({"Name", "Status", "CurrentTime", "StartTime", "UID", "UpTime", "Properties", "Dependencies", "$metadata"})
public final class StatusApiModel {
    private StatusResultApiModel status;
    private Hashtable<String, String> properties;
    private Hashtable<String, StatusResultApiModel> dependencies;
    private DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");

    public StatusApiModel(final StatusServiceModel statusServiceModel) {
        this.status = new StatusResultApiModel(statusServiceModel.getStatus());
        this.dependencies = new Hashtable<>();
        statusServiceModel.getDependencies().forEach((k, v) -> {
            this.dependencies.put(k, new StatusResultApiModel(v));
        });
        this.properties = statusServiceModel.getProperties();
    }

    @JsonProperty("Name")
    public String getName() {
        return "StorageAdapter";
    }

    @JsonProperty("Status")
    public StatusResultApiModel getStatus() {
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
        return this.properties;
    }

    @JsonProperty("Dependencies")
    public Dictionary<String, StatusResultApiModel> getDependencies() {
        return this.dependencies;
    }

    @JsonProperty("$metadata")
    public Dictionary<String, String> getMetadata() {
        return new Hashtable<String, String>() {{
            put("$type", "Status;" + Version.Number);
            put("$uri", "/" + Version.Path + "/status");
        }};
    }
}
