// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.RuleServiceModel;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Public model used by the web service.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class RuleApiModel {

    private String eTag;
    private String id;
    private String name;
    private DateTime dateCreated;
    private DateTime dateModified;
    private boolean enabled;
    private String description;
    private ConditionListApiModel conditions;
    private ActionApiModel action;

    private DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");

    /**
     * Create an instance given the property values.
     *
     * @param eTag
     * @param id
     * @param name
     * @param dateCreated
     * @param dateModified
     * @param enabled
     * @param description
     * @param conditions
     * @param action
     */
    public RuleApiModel(
        final String eTag,
        final String id,
        final String name,
        final DateTime dateCreated,
        final DateTime dateModified,
        final boolean enabled,
        final String description,
        final ConditionListApiModel conditions,
        final ActionApiModel action
    ) {
        this.eTag = eTag;
        this.id = id;
        this.name = name;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
        this.enabled = enabled;
        this.description = description;
        this.conditions = conditions;
        this.action = action;
    }

    /**
     * Create instance given a service model.
     *
     * @param rule service model
     */
    public RuleApiModel(final RuleServiceModel rule) {
        if (rule != null) {
            this.eTag = rule.getETag();
            this.id = rule.getId();
            this.name = rule.getName();
            this.dateCreated = rule.getDateCreated();
            this.dateModified = rule.getDateModified();
            this.enabled = rule.getEnabled();
            this.description = rule.getDescription();
            this.conditions = new ConditionListApiModel(rule.getConditions());
            this.action = new ActionApiModel(rule.getAction());
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

    @JsonProperty("Name")
    public String getName() {
        return this.name;
    }

    @JsonProperty("DateCreated")
    public String getDateCreated() {
        return dateFormat.print(this.dateCreated.toDateTime(DateTimeZone.UTC));
    }

    @JsonProperty("DateModified")
    public String getDateModified() {
        return dateFormat.print(this.dateModified.toDateTime(DateTimeZone.UTC));
    }

    @JsonProperty("Enabled")
    public boolean getEnabled() {
        return this.enabled;
    }

    @JsonProperty("Description")
    public String getDescription() {
        return this.description;
    }

    @JsonProperty("Conditions")
    public ConditionListApiModel getConditions() {
        return this.conditions;
    }

    @JsonProperty("Action")
    public ActionApiModel getAction() {
        return this.action;
    }
}
