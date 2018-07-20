// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.models;

import com.microsoft.azure.documentdb.Document;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.AlarmByRuleApiModel;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public final class AlarmServiceModel {
    private final String eTag;
    private final String id;
    private final DateTime dateCreated;
    private final DateTime dateModified;
    private final String description;
    private final String groupId;
    private final String deviceId;
    private final String status;
    private final String ruleId;
    private final String ruleSeverity;
    private final String ruleDescription;

    public AlarmServiceModel() {
        this.eTag = null;
        this.id = null;
        this.dateCreated = null;
        this.dateModified = null;
        this.description = null;
        this.groupId = null;
        this.deviceId = null;
        this.status = null;
        this.ruleId = null;
        this.ruleSeverity = null;
        this.ruleDescription = null;
    }

    public AlarmServiceModel(
        final String eTag,
        final String id,
        final long dateCreated,
        final long dateModified,
        final String description,
        final String groupId,
        final String deviceId,
        final String status,
        final String ruleId,
        final String ruleSeverity,
        final String ruleDescription) {
        this.eTag = eTag;
        this.id = id;
        this.dateCreated = new DateTime(dateCreated, DateTimeZone.UTC);
        this.dateModified = new DateTime(dateModified, DateTimeZone.UTC);
        this.description = description;
        this.groupId = groupId;
        this.deviceId = deviceId;
        this.status = status;
        this.ruleId = ruleId;
        this.ruleSeverity = ruleSeverity;
        this.ruleDescription = ruleDescription;
    }

    public AlarmServiceModel(Document doc) {
        if (doc != null) {
            this.eTag = doc.getETag();
            this.id = doc.getId();
            this.dateCreated = new DateTime(doc.getLong("created"), DateTimeZone.UTC);
            this.dateModified = new DateTime(doc.getLong("modified"), DateTimeZone.UTC);
            this.description = doc.getString("description");
            this.groupId = doc.getString("group.id");
            this.deviceId = doc.getString("device.id");
            this.status = doc.getString("status");
            this.ruleId = doc.getString("rule.id");
            this.ruleSeverity = doc.getString("rule.severity");
            this.ruleDescription = doc.getString("rule.description");
        } else {
            this.eTag = null;
            this.id = null;
            this.dateCreated = null;
            this.dateModified = null;
            this.description = null;
            this.groupId = null;
            this.deviceId = null;
            this.status = null;
            this.ruleId = null;
            this.ruleSeverity = null;
            this.ruleDescription = null;
        }
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

    public String getStatus() {
        return this.status;
    }

    public String getRuleId() {
        return this.ruleId;
    }

    public String getRuleSeverity() {
        return this.ruleSeverity;
    }

    public String getRuleDescription() {
        return this.ruleDescription;
    }
}
