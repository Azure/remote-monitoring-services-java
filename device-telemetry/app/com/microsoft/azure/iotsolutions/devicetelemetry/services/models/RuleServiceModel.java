// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.joda.time.DateTime;

import java.util.ArrayList;

public final class RuleServiceModel implements Comparable<RuleServiceModel> {

    private final String eTag;
    private final String id;
    private final String name;
    private final String dateCreated;
    private final String dateModified;
    private final Boolean enabled;
    private final String description;
    private final String groupId;
    private final String severity;

    private final ArrayList<ConditionServiceModel> conditions;

    public RuleServiceModel() {
        this.eTag = null;
        this.id = null;
        this.name = null;
        this.dateCreated = null;
        this.dateModified = null;
        this.enabled = null;
        this.description = null;
        this.groupId = null;
        this.severity = null;

        this.conditions = null;
    }

    public RuleServiceModel(
        final String name,
        final Boolean enabled,
        final String description,
        final String groupId,
        final String severity,
        final ArrayList<ConditionServiceModel> conditions) {

        this.name = name;
        this.enabled = enabled;
        this.description = description;
        this.groupId = groupId;
        this.severity = severity;
        this.conditions = conditions;

        this.eTag = "";
        this.id = "";
        this.dateCreated = DateTime.now().toString();
        this.dateModified = DateTime.now().toString();
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
        final String severity,
        final ArrayList<ConditionServiceModel> conditions) {

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

    public String getETag() {
        return this.eTag;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDateCreated() {
        return this.dateCreated;
    }

    public String getDateModified() {
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

    public String getSeverity() {
        return this.severity;
    }

    public ArrayList<ConditionServiceModel> getConditions() {
        return this.conditions;
    }

    @Override
    public int compareTo(RuleServiceModel rule) {
        return getDateCreated().compareTo(rule.getDateCreated());
    }

    // returns rule as json with key names in a storage compatible format
    public JsonNode toJson() {
        ObjectNode jsonRule = new ObjectMapper().createObjectNode();

        jsonRule.put("ETag", this.eTag);
        jsonRule.put("Id", this.id);
        jsonRule.put("Name", this.name);
        jsonRule.put("DateCreated", this.dateCreated);
        jsonRule.put("DateModified", this.dateModified);
        jsonRule.put("Enabled", this.enabled);
        jsonRule.put("Description", this.description);
        jsonRule.put("GroupId", this.groupId);
        jsonRule.put("Severity", this.severity);

        ArrayNode jsonConditions = jsonRule.putArray("Conditions");
        for (ConditionServiceModel condition : this.conditions) {
            ObjectNode jsonCondition = new ObjectMapper().createObjectNode();
            jsonCondition.put("Field", condition.getField());
            jsonCondition.put("Operator", condition.getOperator());
            jsonCondition.put("Value", condition.getValue());

            jsonConditions.add(jsonCondition);
        }

        return jsonRule;
    }
}
