// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.models;

import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceTwinDevice;

import java.util.Dictionary;

// TODO: documentation

public final class DeviceTwinServiceModel {

    private final String eTag;
    private final String deviceId;
    private final Dictionary<String, Object> desiredProperties;
    private final Dictionary<String, Object> reportedProperties;
    private final Dictionary<String, Object> tags;
    private final Boolean isSimulated;

    public DeviceTwinServiceModel(
        final String eTag,
        final String deviceId,
        final Dictionary<String, Object> desiredProperties,
        final Dictionary<String, Object> reportedProperties,
        final Dictionary<String, Object> tags,
        final Boolean isSimulated) {

        this.eTag = eTag;
        this.deviceId = deviceId;
        this.desiredProperties = desiredProperties;
        this.reportedProperties = reportedProperties;
        this.tags = tags;
        this.isSimulated = isSimulated;
    }

    public DeviceTwinServiceModel(final DeviceTwinDevice device) {
        this(
            "", // TODO: SDK doesn't provide this value
            device.getDeviceId(),
            null, // TODO
            null, // TODO
            null, // TODO
            null // TODO: device.getTags().contains(...)
        );
    }

    public String getEtag() {
        return eTag;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public Dictionary<String, Object> getDesiredProperties() {
        return desiredProperties;
    }

    public Dictionary<String, Object> getReportedProperties() {
        return reportedProperties;
    }

    public Dictionary<String, Object> getTags() {
        return tags;
    }

    public Boolean getIsSimulated() {
        return isSimulated;
    }
}
