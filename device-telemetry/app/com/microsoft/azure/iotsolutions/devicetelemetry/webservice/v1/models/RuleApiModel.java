// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.CalculationType;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.ConditionServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.RuleServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.SeverityType;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.Version;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.CompletionException;

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
    private String calculation;
    private String timePeriod;
    private ArrayList<ConditionApiModel> conditions;
    private Boolean deleted;

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
     * @param calculation
     * @param timePeriod
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
        String calculation,
        String timePeriod,
        ArrayList<ConditionApiModel> conditions,
        Boolean deleted
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
        this.calculation = calculation;
        this.timePeriod = timePeriod;
        this.conditions = conditions;
        this.deleted = deleted;
    }

    /**
     * Create instance given a service model.
     *
     * @param rule service model
     */
    public RuleApiModel(RuleServiceModel rule, boolean includeDeleted) {
        if (rule != null) {
            this.eTag = rule.getETag();
            this.id = rule.getId();
            this.name = rule.getName();
            this.dateCreated = rule.getDateCreated().toString();
            this.dateModified = rule.getDateModified().toString();
            this.enabled = rule.getEnabled();
            this.description = rule.getDescription();
            this.groupId = rule.getGroupId();
            this.severity = rule.getSeverity().toString();
            this.calculation = rule.getCalculation().toString();
            this.timePeriod = rule.getTimePeriod().toString();
            if (includeDeleted) {
                this.deleted = rule.getDeleted();
            }
            // create listAsync of ConditionApiModel from ConditionServiceModel listAsync
            this.conditions = new ArrayList<>();
            if (rule.getConditions() != null) {
                for (ConditionServiceModel condition : rule.getConditions()) {
                    this.conditions.add(new ConditionApiModel(condition));
                }
            }
        }
    }

    @JsonProperty("ETag")
    //because UpperCamelCaseStrategy will make this Etag instead of the desired ETag
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

    public String getCalculation() {
        return this.calculation;
    }

    public void setCalculation(String calculation) {
        this.calculation = calculation;
    }

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    public String getTimePeriod() {
        return this.timePeriod;
    }

    @JsonDeserialize(as = ArrayList.class, contentAs = ConditionApiModel.class)
    public ArrayList<ConditionApiModel> getConditions() {
        return this.conditions;
    }

    public void setConditions(ArrayList<ConditionApiModel> conditions) {
        this.conditions = conditions;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Boolean isDeleted() {
        return this.deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
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
        SeverityType severity = null;
        CalculationType calculation = null;
        Long timePeriod = null;
        try {
            severity = SeverityType.valueOf(this.severity.toUpperCase());
        } catch (Exception e) {
            throw new CompletionException(
                new InvalidInputException("The value of 'Severity' - '" + this.severity + "' is not valid"));
        }
        try {
            calculation = CalculationType.valueOf(this.calculation.toUpperCase());
        } catch (Exception e) {
            throw new CompletionException(
                new InvalidInputException("The value of 'Calculation' - '" + this.calculation + "' is not valid"));
        }
        if (calculation == CalculationType.AVERAGE && (this.timePeriod.isEmpty() || this.timePeriod == null)) {
            throw new CompletionException(
                new InvalidInputException("The value of 'TimePeriod' - '" + this.timePeriod + "' for 'Calculation' - " + this.calculation + " is not valid"));
        }
        try {
            timePeriod = this.timePeriod.isEmpty() || this.timePeriod == null ? 0 : Long.valueOf(this.timePeriod);
        } catch (Exception e) {
            throw new CompletionException(
                new InvalidInputException("The value of 'TimePeriod' - '" + this.timePeriod + "' is not valid"));
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
            severity,
            calculation,
            timePeriod,
            conditionServiceModels,
            this.deleted == null ? false : this.deleted
        );
    }

    public RuleServiceModel toServiceModel() {
        return toServiceModel(this.id);
    }
}
