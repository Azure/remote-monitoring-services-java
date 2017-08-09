// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.AlarmServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.Version;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Public model used by the web service.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class AlarmApiModel {

    private String eTag;
    private String id;
    private DateTime dateCreated;
    private DateTime dateModified;
    private String description;
    private String groupId;
    private String deviceId;
    private String status;
    private AlarmRuleApiModel rule;

    private DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");

    /**
     * Create an instance given the property values.
     *
     * @param eTag
     * @param id
     * @param dateCreated
     * @param dateModified
     * @param description
     * @param groupId
     * @param deviceId
     * @param severity
     * @param status;
     */
    public AlarmApiModel(
        final String eTag,
        final String id,
        final DateTime dateCreated,
        final DateTime dateModified,
        final String description,
        final String groupId,
        final String deviceId,
        final String severity,
        final String status,
        final AlarmRuleApiModel rule
    ) {
        this.eTag = eTag;
        this.id = id;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
        this.description = description;
        this.groupId = groupId;
        this.deviceId = deviceId;
        this.status = status;
        this.rule = rule;
    }

    /**
     * Create instance given a service model.
     *
     * @param alarm service model
     */
    public AlarmApiModel(final AlarmServiceModel alarm) {
        if (alarm != null) {
            this.eTag = alarm.getETag();
            this.id = alarm.getId();
            this.dateCreated = alarm.getDateCreated();
            this.dateModified = alarm.getDateModified();
            this.description = alarm.getDescription();
            this.groupId = alarm.getGroupId();
            this.deviceId = alarm.getDeviceId();
            this.status = alarm.getStatus();
            this.rule = new AlarmRuleApiModel(alarm.getRuleId(), alarm.getRuleSeverity(), alarm.getRuleDescription());
        }
    }

    @JsonProperty("ETag")
    public String getETag() {
        return this.eTag;
    }

    @JsonProperty("Id")
    public String getId() {
        return this.id;
    }

    @JsonProperty("DateCreated")
    public String getDateCreated() {
        if(this.dateCreated == null) {
            return null;
        }

        return dateFormat.print(this.dateCreated.toDateTime(DateTimeZone.UTC));
    }

    @JsonProperty("DateModified")
    public String getDateModified() {
        if(this.dateModified == null) {
            return null;
        }
        return dateFormat.print(this.dateModified.toDateTime(DateTimeZone.UTC));
    }

    @JsonProperty("Description")
    public String getDescription() {
        return this.description;
    }

    @JsonProperty("GroupId")
    public String getGroupId() {
        return this.groupId;
    }

    @JsonProperty("DeviceId")
    public String getDeviceId() {
        return this.deviceId;
    }

    @JsonProperty("Status")
    public String getStatus() {
        return this.status;
    }

    @JsonProperty("Rule")
    public AlarmRuleApiModel getRule() { return this.rule; }

    @JsonProperty("$metadata")
    public Dictionary<String, String> getMetadata() {
        return new Hashtable<String, String>() {{
            put("$type", "Alarms;" + Version.NAME);
            put("$uri", "/" + Version.NAME + "/alarms/" + id);
        }};
    }
}
