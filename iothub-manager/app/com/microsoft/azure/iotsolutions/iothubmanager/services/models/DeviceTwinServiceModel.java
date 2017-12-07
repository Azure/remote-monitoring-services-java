// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.iothubmanager.services.helpers.HashMapHelper;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceTwinDevice;

import java.util.*;

public final class DeviceTwinServiceModel {

    private String eTag;
    private String deviceId;
    private DeviceTwinProperties properties;
    private HashMap tags;
    private Boolean isSimulated;

    public DeviceTwinServiceModel () {}

    public DeviceTwinServiceModel(
        final String eTag,
        final String deviceId,
        final DeviceTwinProperties properties,
        final HashMap tags,
        final Boolean isSimulated) {

        this.eTag = eTag;
        this.deviceId = deviceId;
        this.properties = properties;
        this.tags = tags;
        this.isSimulated = isSimulated;
    }

    public DeviceTwinServiceModel(final DeviceTwinDevice device) {
        this(
            device.getETag(),
            device.getDeviceId(),
            new DeviceTwinProperties(
                HashMapHelper.setToHashMap(device.getDesiredProperties()),
                HashMapHelper.setToHashMap(device.getReportedProperties())
            ),
            HashMapHelper.setToHashMap(device.getTags()),
            isSimulated(HashMapHelper.setToHashMap(device.getTags()))
        );
    }

    @JsonProperty("ETag")
    public String getETag() {
        return this.eTag;
    }

    @JsonProperty("DeviceId")
    public String getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @JsonProperty("Properties")
    public DeviceTwinProperties getProperties() {
        return this.properties;
    }

    @JsonProperty("Tags")
    public HashMap getTags() {
        return this.tags;
    }

    @JsonProperty("IsSimulated")
    public Boolean getIsSimulated() {
        return isSimulated;
    }

    private static Boolean isSimulated(Map tags) {
        Set<String> keys = tags.keySet();
        return keys.contains("IsSimulated") && tags.get("IsSimulated") == "Y";
    }

    public DeviceTwinDevice toDeviceTwinDevice() {
        DeviceTwinDevice twinDevice = this.getDeviceId() == null || this.getDeviceId().isEmpty()
            ? new DeviceTwinDevice() : new DeviceTwinDevice(this.getDeviceId());

        if (this.getETag() != null) {
            twinDevice.setETag(this.getETag());
        }

        if (this.getTags() != null) {
            twinDevice.setTags(HashMapHelper.mapToSet(this.getTags()));
        }

        if (this.properties != null && this.properties.getDesired() != null)
            twinDevice.setDesiredProperties(HashMapHelper.mapToSet(this.properties.getDesired()));

        return twinDevice;
    }
}
