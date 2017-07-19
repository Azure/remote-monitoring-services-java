// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.models;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

public final class AlarmServiceModel {
    private final String eTag;
    private final String id;
    private final DateTime dateCreated;
    private final DateTime dateModified;
    private final String description;
    private final String groupId;
    private final String deviceId;
    private final String severity;
    private final String status;
    private final AlarmRuleServiceModel rule;

    public AlarmServiceModel() {
        this.eTag = null;
        this.id = null;
        this.dateCreated = null;
        this.dateModified = null;
        this.description = null;
        this.groupId = null;
        this.deviceId = null;
        this.severity = null;
        this.status = null;
        this.rule = null;
    }

    public AlarmServiceModel(
        final String eTag,
        final String id,
        final String dateCreated,
        final String dateModified,
        final String description,
        final String groupId,
        final String deviceId,
        final String severity,
        final String status,
        final AlarmRuleServiceModel rule
    ) {
        this.eTag = eTag;
        this.id = id;
        this.dateCreated = DateTime.parse(dateCreated, ISODateTimeFormat.dateTimeParser().withZoneUTC());
        this.dateModified = DateTime.parse(dateModified, ISODateTimeFormat.dateTimeParser().withZoneUTC());
        this.description = description;
        this.groupId = groupId;
        this.deviceId = deviceId;
        this.severity = severity;
        this.status = status;
        this.rule = rule;
    }

    public String getETag() {
        return this.eTag;
    }

    public String getId() {
        return this.id;
    }

    public DateTime getDateCreated() {
        return this.dateCreated;
    }

    public DateTime getDateModified() {
        return this.dateModified;
    }

    public String getDescription() {
        return this.description;
    }

    public String getGroupId() {
        return this.groupId;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public String getSeverity() {
        return this.severity;
    }

    public String getStatus() {
        return this.status;
    }

    public AlarmRuleServiceModel getRule() {
        return this.rule;
    }
}
