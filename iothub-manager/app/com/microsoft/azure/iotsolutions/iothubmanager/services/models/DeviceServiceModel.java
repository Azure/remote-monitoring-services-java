// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.models;

import com.microsoft.azure.sdk.iot.service.*;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

// TODO: documentation
// TODO: datetime parsing

public final class DeviceServiceModel {

    private final String eTag;
    private final String id;
    private final long c2DMessageCount;
    private final DateTime lastActivity;
    private final Boolean connected;
    private final Boolean enabled;
    private final DateTime lastStatusUpdated;
    private final DeviceTwinServiceModel twin;

    public DeviceServiceModel(
        final String eTag,
        final String id,
        final long c2DMessageCount,
        final String lastActivity,
        final Boolean connected,
        final Boolean enabled,
        final String lastStatusUpdated,
        final DeviceTwinServiceModel twin) {

        this.eTag = eTag;
        this.id = id;
        this.c2DMessageCount = c2DMessageCount;
        this.lastActivity = DateTime.parse(lastActivity, ISODateTimeFormat.dateTimeParser().withZoneUTC());
        this.connected = connected;
        this.enabled = enabled;
        this.lastStatusUpdated = DateTime.parse(lastStatusUpdated, ISODateTimeFormat.dateTimeParser().withZoneUTC());
        this.twin = twin;
    }

    public DeviceServiceModel(final Device device, final DeviceTwinServiceModel twin) {
        this(
            device.geteTag(),
            device.getDeviceId(),
            device.getCloudToDeviceMessageCount(),
            device.getLastActivityTime(),
            device.getConnectionState() == DeviceConnectionState.Connected,
            device.getStatus() == DeviceStatus.Enabled,
            device.getStatusUpdatedTime(),
            twin);
    }

    public String getETag() {
        return eTag;
    }

    public String getId() {
        return id;
    }

    public long getC2DMessageCount() {
        return c2DMessageCount;
    }

    public DateTime getLastActivity() {
        return lastActivity;
    }

    public Boolean getConnected() {
        return connected;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public DateTime getLastStatusUpdated() {
        return lastStatusUpdated;
    }

    public DeviceTwinServiceModel getTwin() {
        return twin;
    }
}
