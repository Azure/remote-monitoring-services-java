// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceServiceModel;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.Version;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Dictionary;
import java.util.Hashtable;

// TODO: documentation
// TODO: see https://github.com/FasterXML/jackson-annotations for JSON annotations

/**
 * Public model used by the web service.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class DeviceApiModel {

    private String eTag = null;
    private String id = null;
    private boolean enabled = false;
    private DeviceTwinApiModel twin = null;
    private long c2DMessageCount = 0;
    private DateTime lastActivity = null;
    private boolean connected = false;
    private DateTime lastStatusUpdated = null;

    private DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");

    /**
     * Create an instance given the property values.
     *
     * @param eTag              Entity tag
     * @param id                Device id
     * @param c2DMessageCount
     * @param lastActivity
     * @param connected
     * @param enabled
     * @param lastStatusUpdated
     * @param twin
     */
    public DeviceApiModel(
        final String eTag,
        final String id,
        final long c2DMessageCount,
        final DateTime lastActivity,
        final boolean connected,
        final boolean enabled,
        final DateTime lastStatusUpdated,
        final DeviceTwinApiModel twin) {

        this.eTag = eTag;
        this.id = id;
        this.enabled = enabled;
        this.twin = twin;
        this.c2DMessageCount = c2DMessageCount;
        this.lastActivity = lastActivity;
        this.connected = connected;
        this.lastStatusUpdated = lastStatusUpdated;
    }

    /**
     * Create instance given a service model.
     *
     * @param device Service model
     */
    public DeviceApiModel(final DeviceServiceModel device) {
        this(
            device.getETag(),
            device.getId(),
            device.getC2DMessageCount(),
            device.getLastActivity(),
            device.getConnected(),
            device.getEnabled(),
            device.getLastStatusUpdated(),
            new DeviceTwinApiModel(device.getId(), device.getTwin()));
    }

    @JsonProperty("Id")
    public String getId() {
        return this.id;
    }

    @JsonProperty("Id")
    public void setId(String value) {
        this.id = value;
    }

    @JsonProperty("ETag")
    public String getETag() {
        return this.eTag;
    }

    @JsonProperty("ETag")
    public void setETag(String value) {
        this.eTag = value;
    }

    @JsonProperty("Enabled")
    public boolean getEnabled() {
        return this.enabled;
    }

    @JsonProperty("Enabled")
    public void setEnabled(Boolean value) {
        this.enabled = value;
    }

    @JsonProperty("Twin")
    public DeviceTwinApiModel getTwin() {
        return twin;
    }

    @JsonProperty("Twin")
    public void setTwin(DeviceTwinApiModel value) {
        this.twin = value;
    }

    @JsonProperty("C2DMessageCount")
    public long getC2DMessageCount() {
        return this.c2DMessageCount;
    }

    @JsonProperty("LastActivity")
    public String getLastActivityAsString() {
        return dateFormat.print(this.lastActivity.toDateTime(DateTimeZone.UTC));
    }

    @JsonProperty("Connected")
    public boolean getConnected() {
        return this.connected;
    }

    @JsonProperty("LastStatusUpdated")
    public String getLastStatusUpdatedAsString() {
        return dateFormat.print(this.lastStatusUpdated);
    }

    @JsonProperty("$metadata")
    public Dictionary<String, String> getMetadata() {
        String id = this.getId();
        return new Hashtable<String, String>() {{
            put("$type", "Device;" + Version.NAME);
            put("$uri", "/" + Version.NAME + "/devices/" + id);
        }};
    }

    public DeviceServiceModel toServiceModel() {
        return new DeviceServiceModel(
            this.getETag(),
            this.getId(),
            this.getC2DMessageCount(),
            this.getLastActivityAsString(),
            this.getConnected(),
            this.getEnabled(),
            this.getLastStatusUpdatedAsString(),
            this.getTwin().toServiceModel()
        );
    }
}
