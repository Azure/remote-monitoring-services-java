// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.Status;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.runtime.Uptime;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.Version;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.*;
import java.util.stream.Collectors;

@JsonPropertyOrder({"Name", "Status", "CurrentTime", "StartTime", "UpTime", "Properties", "Dependencies", "$metadata"})
public final class StatusApiModel {

    private String status;
    private DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");
    private List<Status> statusList;
    private HashMap<String, String> properties = new HashMap<String, String>() {{
        put("Simulation", "on");
        put("Region", "US");
        put("DebugMode", "off");
    }};

    public StatusApiModel() {
        this.statusList = new ArrayList<>();
        setStatusMessage();
    }

    @JsonProperty("Name")
    public String getName() {
        return "telemetry";
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
    public HashMap<String, String> getProperties() {
        return this.properties;
    }

    @JsonProperty("Dependencies")
    public HashMap<String, String> getDependencies() {
        HashMap<String, String> dependencies = new HashMap<>();

        this.statusList.stream()
            .forEach(s -> {
                String dependencyStatusMessage = s.isHealthy() ? "OK" : "ERROR";
                dependencies.put(s.getName(), dependencyStatusMessage + ": " + s.getStatusMessage());
            });
        return dependencies;
    }

    @JsonProperty("$metadata")
    public HashMap<String, String> getMetadata() {
        return new HashMap<String, String>() {{
            put("$type", "Status;" + Version.NAME);
            put("$uri", "/" + Version.NAME + "/status");
        }};
    }

    public void addStatus(Status status) {
        this.statusList.add(status);
        this.setStatusMessage();
    }

    private void setStatusMessage() {
        // check dependency health
        boolean isHealthy = this.statusList.stream()
            .allMatch(s -> s.isHealthy());

        this.status = isHealthy ? "OK:Alive and well" : String.join(";",
            this.statusList.stream()
                .filter(s -> !s.isHealthy())
                .map(s -> s.getStatusMessage())
                .collect(Collectors.toList())
        );
    }
}
