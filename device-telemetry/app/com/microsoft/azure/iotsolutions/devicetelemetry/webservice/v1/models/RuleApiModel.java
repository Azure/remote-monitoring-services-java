// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.ConditionServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.RuleServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.Version;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Public model used by the web service.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class RuleApiModel {

    private String eTag;
    private String id;
    private String name;
    private String dateCreated;
    private String dateModified;
    private boolean enabled;
    private String description;
    private String groupId;
    private String severity;
    private ArrayList<ConditionApiModel> conditions;

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
     * @param groupId
     * @param severity
     * @param conditions
     */
    public RuleApiModel(
        final String eTag,
        final String id,
        final String name,
        final String dateCreated,
        final String dateModified,
        final boolean enabled,
        final String description,
        final String groupId,
        final String severity,
        final ArrayList<ConditionApiModel> conditions
    ) {
        this.eTag = eTag;
        this.id = id;
        this.name = name;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
        this.enabled = enabled;
        this.description = description;
        this.groupId = groupId;
        this.severity = severity;
        this.conditions = conditions;
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
            this.dateCreated = rule.getDateCreated().toString();
            this.dateModified = rule.getDateModified().toString();
            this.enabled = rule.getEnabled();
            this.description = rule.getDescription();
            this.groupId = rule.getGroupId();
            this.severity = rule.getSeverity();

            // create listAsync of ConditionApiModel from ConditionServiceModel listAsync
            this.conditions = new ArrayList<>();
            for (ConditionServiceModel condition : rule.getConditions()) {
                this.conditions.add(new ConditionApiModel(condition));
            }
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
        return dateCreated;
    }

    @JsonProperty("DateModified")
    public String getDateModified() {
        return dateModified;
    }

    @JsonProperty("Enabled")
    public boolean getEnabled() {
        return this.enabled;
    }

    @JsonProperty("Description")
    public String getDescription() {
        return this.description;
    }

    @JsonProperty("GroupId")
    public String getGroupId() {
        return this.groupId;
    }

    @JsonProperty("Severity")
    public String getSeverity() {
        return this.severity;
    }

    @JsonProperty("Conditions")
    public ArrayList<ConditionApiModel> getConditions() {
        return this.conditions;
    }

    @JsonProperty("$metadata")
    public Dictionary<String, String> getMetadata() {
        return new Hashtable<String, String>() {{
            put("$type", "Rule;" + Version.NAME);
            put("$uri", "/" + Version.NAME + "/rules/" + id);
        }};
    }

    public RuleServiceModel toServiceModel() {
        ArrayList<ConditionServiceModel> conditionServiceModelArrayList =
            new ArrayList<>();

        for (ConditionApiModel condition : this.getConditions()) {
            conditionServiceModelArrayList.add(condition.toServiceModel());
        }

        return new RuleServiceModel(
            this.getETag(),
            this.getId(),
            this.getName(),
            this.getDateCreated(),
            this.getDateModified(),
            this.getEnabled(),
            this.getDescription(),
            this.getGroupId(),
            this.getSeverity(),
            conditionServiceModelArrayList);
    }
}
