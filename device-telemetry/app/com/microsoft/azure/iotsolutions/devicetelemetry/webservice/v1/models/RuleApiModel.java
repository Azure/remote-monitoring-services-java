// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
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

    public RuleApiModel() {

    }

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
        String eTag,
        String id,
        String name,
        String dateCreated,
        String dateModified,
        boolean enabled,
        String description,
        String groupId,
        String severity,
        ArrayList<ConditionApiModel> conditions
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
    public RuleApiModel(RuleServiceModel rule) {
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
            if (rule.getConditions() != null) {
                for (ConditionServiceModel condition : rule.getConditions()) {
                    this.conditions.add(new ConditionApiModel(condition));
                }
            }
        }
    }

    @JsonProperty("ETag") //because UpperCamelCaseStrategy will make this Etag instead of the desired ETag
    public String getETag() {
        return this.eTag;
    }

    public void setETag(String eTag) {
        this.eTag = eTag;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGroupId() {
        return this.groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getSeverity() {
        return this.severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    @JsonDeserialize(as = ArrayList.class, contentAs = ConditionApiModel.class)
    public ArrayList<ConditionApiModel> getConditions() {
        return this.conditions;
    }

    public void setConditions(ArrayList<ConditionApiModel> conditions) {
        this.conditions = conditions;
    }

    @JsonProperty("$metadata")
    public Dictionary<String, String> getMetadata() {
        return new Hashtable<String, String>() {{
            put("$type", "Rule;" + Version.NAME);
            put("$uri", "/" + Version.NAME + "/rules/" + id);
        }};
    }

    /**
     * Convert to the service model
     *
     * @param idOverride Allows for overriding the Id when doing an update to ensure the Id from the route matches the one in the item being updated.
     *
     * @return
     */
    public RuleServiceModel toServiceModel(String idOverride) {
        ArrayList<ConditionServiceModel> conditionServiceModels = new ArrayList<ConditionServiceModel>();
        if (conditions != null) {
            for (ConditionApiModel condition :
                conditions) {
                conditionServiceModels.add(condition.toServiceModel());
            }
        }

        return new RuleServiceModel(
            this.eTag,
            idOverride,
            this.name,
            this.dateCreated,
            this.dateModified,
            this.enabled,
            this.description,
            this.groupId,
            this.severity,
            conditionServiceModels
        );
    }

    public RuleServiceModel toServiceModel() {
        return toServiceModel(this.id);
    }
}
