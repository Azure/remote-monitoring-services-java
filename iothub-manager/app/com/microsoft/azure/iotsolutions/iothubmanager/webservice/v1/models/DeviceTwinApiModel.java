// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceTwinServiceModel;
import com.microsoft.azure.iotsolutions.iothubmanager.webservice.v1.Version;

import java.util.Dictionary;
import java.util.Hashtable;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class DeviceTwinApiModel {

    private String eTag;
    private String deviceId;
    private Hashtable<String, Object> tags;

    public DeviceTwinApiModel(final String id, final DeviceTwinServiceModel twin) {

        if (twin != null) {
            this.eTag = twin.getEtag();
        } else {
            this.eTag = "";
        }

        this.deviceId = id;
        this.tags = new Hashtable<String, Object>();
    }

    @JsonProperty("DeviceId")
    public String getDeviceId() {
        return this.deviceId;
    }

    @JsonProperty("DeviceId")
    public void setDeviceId(String value) {
        this.deviceId = value;
    }

    @JsonProperty("Etag")
    public String getETag() {
        return this.eTag;
    }

    @JsonProperty("Etag")
    public void setETag(String value) {
        this.eTag = value;
    }

    @JsonProperty("Tags")
    public Hashtable<String, Object> getTags() {
        return this.tags;
    }

    @JsonProperty("Tags")
    public void setTags(Hashtable<String, Object> value) {
        this.tags = value;
    }

    @JsonProperty("$metadata")
    public Dictionary<String, String> getMetadata() {
        String id = this.getDeviceId();
        return new Hashtable<String, String>() {{
            put("$type", "DeviceTwin;" + Version.NAME);
            put("$uri", "/" + Version.NAME + "/devices/" + id);
        }};
    }

    public DeviceTwinServiceModel toServiceModel() {
        // TODO
        return null;
    }
}
