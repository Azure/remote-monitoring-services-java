// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.models;

import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.InvalidInputException;
import com.microsoft.azure.sdk.iot.deps.twin.DeviceCapabilities;
import com.microsoft.azure.sdk.iot.service.Device;
import com.microsoft.azure.sdk.iot.service.DeviceConnectionState;
import com.microsoft.azure.sdk.iot.service.DeviceStatus;
import com.microsoft.azure.sdk.iot.service.auth.SymmetricKey;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

public final class DeviceServiceModel {

    private final String eTag;
    private String id;
    private final long c2DMessageCount;
    private final DateTime lastActivity;
    private final Boolean connected;
    private final Boolean enabled;
    private final Boolean isEdgeDevice;
    private final DateTime lastStatusUpdated;
    private final TwinServiceModel twin;
    private final String ioTHubHostName;
    private final AuthenticationMechanismServiceModel authentication;

    public DeviceServiceModel(
            final String eTag,
            String id,
            final long c2DMessageCount,
            final DateTime lastActivity,
            final Boolean connected,
            final Boolean enabled,
            final Boolean isEdgeDevice,
            final DateTime lastStatusUpdated,
            final TwinServiceModel twin,
            final AuthenticationMechanismServiceModel authentication,
            final String iotHubHostName) {

        this.eTag = eTag;
        this.id = id;
        this.c2DMessageCount = c2DMessageCount;
        this.lastActivity = lastActivity;
        this.connected = connected;
        this.enabled = enabled;
        this.isEdgeDevice = isEdgeDevice;
        this.lastStatusUpdated = lastStatusUpdated;
        this.twin = twin;
        this.authentication = authentication;
        this.ioTHubHostName = iotHubHostName;
    }

    public DeviceServiceModel(final Device device, final TwinServiceModel twin, String iotHubHostName) {
        this(
                device,
                twin,
                iotHubHostName,
                false);
    }

    public DeviceServiceModel(final Device device, final TwinServiceModel twin, String iotHubHostName,
                              boolean isConnectedEdgeDevice) {
        this(
                device.geteTag(),
                device.getDeviceId(),
                device.getCloudToDeviceMessageCount(),
                device.getLastActivityTime() == null ? null : DateTime.parse(device.getLastActivityTime(), ISODateTimeFormat.dateTimeParser().withZoneUTC()),
                isConnectedEdgeDevice || device.getConnectionState() == DeviceConnectionState.Connected,
                device.getStatus() == DeviceStatus.Enabled,
                device.getCapabilities() != null ? device.getCapabilities().isIotEdge() : twin.getIsEdgeDevice(),
                device.getStatusUpdatedTime() == null ? null : DateTime.parse(device.getStatusUpdatedTime(), ISODateTimeFormat.dateTimeParser().withZoneUTC()),
                twin,
                new AuthenticationMechanismServiceModel(device),
                iotHubHostName);
    }

    public String getETag() {
        return eTag;
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        this.id = value;
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

    public Boolean getIsEdgeDevice() {
        return this.isEdgeDevice;
    }

    public DateTime getLastStatusUpdated() {
        return lastStatusUpdated;
    }

    public TwinServiceModel getTwin() {
        return twin;
    }

    public String getIoTHubHostName() {
        return this.ioTHubHostName;
    }

    public AuthenticationMechanismServiceModel getAuthentication() {
        return this.authentication;
    }

    public Device toAzureModel() throws InvalidInputException {
        try {
            final Device createdDevice;
            if (this.authentication == null || this.authentication.getAuthenticationType() == null) {
                createdDevice = Device.createFromId(
                        this.getId(),
                        this.getEnabled() ? DeviceStatus.Enabled : DeviceStatus.Disabled,
                        new SymmetricKey());
            } else if (this.authentication.getAuthenticationType() == AuthenticationType.Sas) {
                SymmetricKey key = new SymmetricKey();
                key.setPrimaryKey(this.authentication.getPrimaryKey());
                key.setSecondaryKey(this.authentication.getSecondaryKey());
                createdDevice = Device.createFromId(
                        this.getId(),
                        this.getEnabled() ? DeviceStatus.Enabled : DeviceStatus.Disabled,
                        key);
            } else {
                createdDevice = Device.createDevice(this.getId(),
                        AuthenticationType.toAzureModel(this.authentication.getAuthenticationType()));
                createdDevice.setThumbprint(
                        this.getAuthentication().getPrimaryThumbprint(),
                        this.getAuthentication().getSecondaryThumbprint());
            }

            if (this.isEdgeDevice) {
                createdDevice.setCapabilities(new DeviceCapabilities());
                createdDevice.getCapabilities().setIotEdge(true);
            }

            return createdDevice;
        } catch (Exception e) {
            throw new InvalidInputException("Unable to create device", e);
        }
    }
}
