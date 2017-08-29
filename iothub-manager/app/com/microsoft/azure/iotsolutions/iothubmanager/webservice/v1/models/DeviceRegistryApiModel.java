// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models;

import com.fasterxml.jackson.annotation.*;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.*;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.Version;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.*;

/**
 * Public model used by the web service.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class DeviceRegistryApiModel {

    private String eTag = null;
    private String id = null;
    private boolean enabled = false;
    private long c2DMessageCount = 0;
    private DateTime lastActivity = null;
    private boolean connected = false;
    private DateTime lastStatusUpdated = null;
    private String authPrimaryKey = null;
    private String ioTHubHostName = null;
    private HashMap<String, Object> tags;
    private DeviceTwinProperties properties;
    private boolean isSimulated;

    private DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");

    public DeviceRegistryApiModel() {
    }

    /**
     * Create instance given a service model.
     *
     * @param device Service model
     */
    public DeviceRegistryApiModel(final DeviceServiceModel device) {
        if (device == null) return;

        this.id = device.getId();
        this.eTag = device.getETag();
        this.c2DMessageCount = device.getC2DMessageCount();
        this.lastActivity = device.getLastActivity();
        this.connected = device.getConnected();
        this.enabled = device.getEnabled();
        this.lastStatusUpdated = device.getLastStatusUpdated();
        this.authPrimaryKey = device.getAuthPrimaryKey();
        this.ioTHubHostName = device.getIoTHubHostName();

        DeviceTwinServiceModel twinModel = device.getTwin();
        if (twinModel != null) {
            this.eTag = this.eTag + "|" + device.getTwin().getEtag();
            this.properties = twinModel.getProperties();
            this.tags = device.getTwin().getTags();
            this.isSimulated = device.getTwin().getIsSimulated();
        }
    }

    @JsonProperty("Id")
    public String getId() {
        return this.id;
    }

    public void setId(String value) {
        this.id = value;
    }

    @JsonProperty("ETag")
    public String getETag() {
        return this.eTag;
    }

    public void setETag(String value) {
        this.eTag = value;
    }

    @JsonProperty("Enabled")
    public boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(Boolean value) {
        this.enabled = value;
    }

    @JsonProperty("C2DMessageCount")
    public long getC2DMessageCount() {
        return this.c2DMessageCount;
    }

    @JsonProperty("LastActivity")
    public String getLastActivityAsString() {
        if (this.lastActivity == null) {
            return null;
        }
        return dateFormat.print(this.lastActivity.toDateTime(DateTimeZone.UTC));
    }

    @JsonProperty("Connected")
    public boolean getConnected() {
        return this.connected;
    }

    @JsonProperty("LastStatusUpdated")
    public String getLastStatusUpdatedAsString() {
        if (this.lastStatusUpdated == null) {
            return null;
        }
        return dateFormat.print(this.lastStatusUpdated);
    }

    @JsonProperty("AuthPrimaryKey")
    public String getAuthPrimaryKey() {
        return this.authPrimaryKey;
    }

    public void setAuthPrimaryKey(String authPrimaryKey) {
        this.authPrimaryKey = authPrimaryKey;
    }

    @JsonProperty("IoTHubHostName")
    public String getIoTHubHostName() {
        return this.ioTHubHostName;
    }

    public void setIoTHubHostName(String ioTHubHostName) {
        this.ioTHubHostName = ioTHubHostName;
    }

    @JsonProperty("$metadata")
    public Dictionary<String, String> getMetadata() {
        String id = this.getId();
        return new Hashtable<String, String>() {{
            put("$type", "Device;" + Version.NUMBER);
            put("$uri", "/" + Version.PATH + "/devices/" + id);
        }};
    }

    @JsonProperty("Tags")
    public HashMap<String, Object> getTags() {
        return this.tags;
    }

    public void setTags(HashMap<String, Object> value) {
        this.tags = value;
    }

    @JsonProperty("Properties")
    public DeviceTwinProperties getProperties() {
        return this.properties;
    }

    public void setProperties(DeviceTwinProperties value) {
        this.properties = value;
    }

    @JsonProperty("IsSimulated")
    public boolean isSimulated() {
        return isSimulated;
    }

    private void setSimulated(boolean simulated) {
        isSimulated = simulated;
    }

    public DeviceServiceModel toServiceModel() {
        DeviceTwinServiceModel twinServiceModel = new DeviceTwinServiceModel(
            this.eTag,
            this.id,
            this.properties,
            this.tags,
            this.isSimulated);

        return new DeviceServiceModel(
            this.getETag(),
            this.getId(),
            this.getC2DMessageCount(),
            this.getLastActivityAsString(),
            this.getConnected(),
            this.getEnabled(),
            this.getLastStatusUpdatedAsString(),
            twinServiceModel,
            this.authPrimaryKey,
            this.ioTHubHostName
        );
    }
}
