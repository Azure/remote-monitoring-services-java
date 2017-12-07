// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models;

import com.fasterxml.jackson.annotation.*;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.*;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.Version;
import org.joda.time.DateTime;

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
    private Date lastActivity = null;
    private boolean connected = false;
    private Date lastStatusUpdated = null;
    private AuthenticationMechanismApiModel authentication = null;
    private String ioTHubHostName = null;
    private HashMap<String, Object> tags;
    private DeviceTwinProperties properties;
    private boolean isSimulated;

    private final String dateFormatString = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

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
        this.lastActivity = device.getLastActivity().toDate();
        this.connected = device.getConnected();
        this.enabled = device.getEnabled();
        this.lastStatusUpdated = device.getLastStatusUpdated().toDate();
        this.authentication = new AuthenticationMechanismApiModel(device.getAuthentication());
        this.ioTHubHostName = device.getIoTHubHostName();

        DeviceTwinServiceModel twinModel = device.getTwin();
        if (twinModel != null) {
            this.eTag = this.eTag + "|" + device.getTwin().getETag();
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = dateFormatString)
    public Date getLastActivity() {
        return this.lastActivity;
    }

    public void setLastActivity(Date value) {
        this.lastActivity = value;
    }

    @JsonProperty("Connected")
    public boolean getConnected() {
        return this.connected;
    }

    @JsonProperty("LastStatusUpdated")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = dateFormatString)
    public Date getLastStatusUpdated() {
        return this.lastStatusUpdated;
    }

    public void setLastStatusUpdated(Date value) {
        this.lastActivity = value;
    }

    @JsonProperty("Authentication")
    public AuthenticationMechanismApiModel getAuthentication() {
        return this.authentication;
    }

    public void setAuthentication(AuthenticationMechanismApiModel authentication) {
        this.authentication = authentication;
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
            put("$twin_uri", "/" + Version.PATH + "/devices/" + id + "/twin");
        }};
    }

    @JsonProperty("Tags")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public HashMap<String, Object> getTags() {
        return this.tags;
    }

    public void setTags(HashMap<String, Object> value) {
        this.tags = value;
    }

    @JsonProperty("Properties")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public DeviceTwinProperties getProperties() {
        return this.properties;
    }

    public void setProperties(DeviceTwinProperties value) {
        this.properties = value;
    }

    @JsonProperty("IsSimulated")
    @JsonInclude(JsonInclude.Include.NON_NULL)
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
            this.eTag,
            this.id,
            this.c2DMessageCount,
            new DateTime(this.lastActivity),
            this.connected,
            this.enabled,
            new DateTime(this.lastStatusUpdated),
            twinServiceModel,
            this.authentication == null ? null : this.authentication.toServiceModel(),
            this.ioTHubHostName
        );
    }
}
