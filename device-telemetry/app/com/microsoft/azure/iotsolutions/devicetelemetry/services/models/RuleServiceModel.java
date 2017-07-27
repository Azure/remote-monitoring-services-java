// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.models;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.util.ArrayList;

public final class RuleServiceModel {

    private final String eTag;
    private final String id;
    private final String name;
    private final DateTime dateCreated;
    private final DateTime dateModified;
    private final Boolean enabled;
    private final String description;
    private final String groupId;

    private final ArrayList<ConditionServiceModel> conditions;
    private final ActionServiceModel action;

    public RuleServiceModel() {
        this.eTag = null;
        this.id = null;
        this.name = null;
        this.dateCreated = null;
        this.dateModified = null;
        this.enabled = null;
        this.description = null;
        this.groupId = null;

        this.conditions = null;
        this.action = null;
    }

    public RuleServiceModel(
        final String eTag,
        final String id,
        final String name,
        final String dateCreated,
        final String dateModified,
        final Boolean enabled,
        final String description,
        final String groupId,
        final ArrayList<ConditionServiceModel> conditions,
        final ActionServiceModel action) {

        this.eTag = eTag;
        this.id = id;
        this.name = name;
        this.dateCreated = DateTime.parse(dateCreated, ISODateTimeFormat.dateTimeParser().withZoneUTC());
        this.dateModified = DateTime.parse(dateModified, ISODateTimeFormat.dateTimeParser().withZoneUTC());
        this.enabled = enabled;
        this.description = description;
        this.groupId = groupId;

        this.conditions = conditions;
        this.action = action;
    }

    public String getETag() {
        return this.eTag;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public DateTime getDateCreated() {
        return this.dateCreated;
    }

    public DateTime getDateModified() {
        return this.dateModified;
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public String getDescription() {
        return this.description;
    }

    public String getGroupId() {
        return this.groupId;
    }

    public ArrayList<ConditionServiceModel> getConditions() {
        return this.conditions;
    }

    public ActionServiceModel getAction() {
        return this.action;
    }
}
