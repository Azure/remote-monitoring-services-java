// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.iothubmanager.services.helpers.HashMapHelper;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceTwinDevice;

import java.util.*;

public final class TwinServiceModel {

    private String eTag;
    private String deviceId;
    private String moduleId;
    private TwinProperties properties;
    private HashMap<String, Object> tags;
    private boolean isEdgeDevice;
    private boolean isSimulated;
    private static final String SIMULATED_KEY = "IsSimulated";

    public TwinServiceModel() {}

    public TwinServiceModel(
        final String eTag,
        final String deviceId,
        final TwinProperties properties,
        final HashMap<String, Object> tags,
        final boolean isSimulated) {
        this(eTag, deviceId, null, properties, tags,
                isSimulated, false);
    }

    public TwinServiceModel(
            final String eTag,
            final String deviceId,
            final String moduleId,
            final TwinProperties properties,
            final HashMap<String, Object> tags,
            final boolean isSimulated,
            final boolean isEdgeDevice) {

        this.eTag = eTag;
        this.deviceId = deviceId;
        this.moduleId = moduleId;
        this.properties = properties;
        this.tags = tags;
        this.isSimulated = isSimulated;
        this.isEdgeDevice = isEdgeDevice;
    }

    public TwinServiceModel(final DeviceTwinDevice device) {
        this(
            device.getETag(),
            device.getDeviceId(),
            device.getModuleId(),
            new TwinProperties(
                HashMapHelper.setToHashMap(device.getDesiredProperties()),
                HashMapHelper.setToHashMap(device.getReportedProperties())
            ),
            HashMapHelper.setToHashMap(device.getTags()),
            isSimulated(HashMapHelper.setToHashMap(device.getTags())),
            device.getCapabilities() != null ? device.getCapabilities().isIotEdge() : false
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

    @JsonProperty("ModuleId")
    public String getModuleId() {
        return this.moduleId;
    }

    @JsonProperty("Properties")
    public TwinProperties getProperties() {
        return this.properties;
    }

    @JsonProperty("Tags")
    public HashMap<String, Object> getTags() {
        return this.tags;
    }

    @JsonProperty("IsSimulated")
    public Boolean getIsSimulated() {
        return this.isSimulated;
    }

    @JsonProperty("IsEdgeDevice")
    public boolean getIsEdgeDevice() {
        return this.isEdgeDevice;
    }

    private static Boolean isSimulated(Map tags) {
        Set<String> keys = tags.keySet();
        return keys.contains(SIMULATED_KEY) && tags.get(SIMULATED_KEY).toString().equalsIgnoreCase("Y");
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

        if (this.isEdgeDevice && twinDevice.getCapabilities() != null) {
            twinDevice.getCapabilities().setIotEdge(this.isEdgeDevice);
        }

        return twinDevice;
    }
}
