// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.Rules;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.IActionServiceModel;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import play.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionException;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
public final class RuleServiceModel implements Comparable<RuleServiceModel> {

    private final static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZZ";
    private static final Logger.ALogger log = Logger.of(Rules.class);

    private String eTag = null;
    private String id = null;
    private String name = null;
    private String dateCreated = null;
    private String dateModified = null;
    private Boolean enabled = null;
    private String description = null;
    private String groupId = null;
    private SeverityType severity = null;
    private CalculationType calculation = null;
    private Long timePeriod = null;
    private boolean deleted = false;

    private ArrayList<ConditionServiceModel> conditions = null;

    private List<IActionServiceModel> actions = null;

    public RuleServiceModel() {
    }

    public RuleServiceModel(
        final String name,
        final Boolean enabled,
        final String description,
        final String groupId,
        final SeverityType severity,
        final CalculationType calculation,
        final Long timePeriod,
        final ArrayList<ConditionServiceModel> conditions) {

        this(
            "",
            "",
            name,
            DateTime.now(DateTimeZone.UTC).toString(DATE_FORMAT),
            DateTime.now(DateTimeZone.UTC).toString(DATE_FORMAT),
            enabled,
            description,
            groupId,
            severity,
            calculation,
            timePeriod,
            conditions,
            false,
            null
        );
    }

    public RuleServiceModel(
        String eTag,
        String id,
        String name,
        String dateCreated,
        String dateModified,
        Boolean enabled,
        String description,
        String groupId,
        SeverityType severity,
        CalculationType calculation,
        Long timePeriod,
        ArrayList<ConditionServiceModel> conditions,
        boolean deleted,
        List<IActionServiceModel> actions) {

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
        this.actions = actions;
    }

    @JsonIgnore
    //comes from the StorageAdapter document and not the serialized rule
    public String getETag() {
        return this.eTag;
    }

    @JsonIgnore
    //comes from the StorageAdapter document and not the serialized rule
    public void setETag(String eTag) {
        this.eTag = eTag;
    }

    @JsonIgnore
    //comes from the StorageAdapter document and not the serialized rule
    public String getId() {
        return this.id;
    }

    @JsonIgnore
    //comes from the StorageAdapter document and not the serialized rule
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
        return this.dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateModified() {
        return this.dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(Boolean enabled) {
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

    public SeverityType getSeverity() {
        return this.severity;
    }

    public void setSeverity(SeverityType severity) {
        this.severity = severity;
    }

    public Long getTimePeriod() {
        return this.timePeriod;
    }

    public void setTimePeriod(Long timePeriod) {
        this.timePeriod = timePeriod;
    }

    public CalculationType getCalculation() {
        return this.calculation;
    }

    public void setCalculation(CalculationType calculation) {
        this.calculation = calculation;
    }

    @JsonDeserialize(as = ArrayList.class, contentAs = ConditionServiceModel.class)
    public ArrayList<ConditionServiceModel> getConditions() {
        return this.conditions;
    }

    public void setConditions(ArrayList<ConditionServiceModel> conditions) {
        this.conditions = conditions;
    }

    public boolean getDeleted() { return this.deleted; }

    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    public List<IActionServiceModel> getActions() { return this.actions; }

    public void setActions(List<IActionServiceModel> actions) { this.actions = actions; }

    @Override
    public int compareTo(RuleServiceModel rule) {
        return getDateCreated().compareTo(rule.getDateCreated());
    }

    // returns rule as json with key names in a storage compatible format
    public String toJsonString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (Exception e) {
            log.error("Could not write object as json string: {}",
                e.getMessage());
            throw new CompletionException(
                new ExternalDependencyException(
                    "Could not write object as json string"));
        }
    }

    public RuleServiceModel overrideEtagAndId(String eTag, String id) {
        RuleServiceModel rule = new RuleServiceModel(
            eTag,
            id,
            this.name,
            this.dateCreated,
            this.dateModified,
            this.enabled,
            this.description,
            this.groupId,
            this.severity,
            this.calculation,
            this.timePeriod,
            this.conditions,
            this.deleted,
            this.actions);
        return rule;
    }
}
