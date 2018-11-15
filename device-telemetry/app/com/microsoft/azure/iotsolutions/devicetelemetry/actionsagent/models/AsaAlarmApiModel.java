// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.IActionServiceModel;

import java.util.List;

public class AsaAlarmApiModel {

    private String dateCreated;
    private String dateModified;
    private String ruleDescription;
    private String ruleSeverity;
    private String ruleId;
    private List<IActionServiceModel> actions;
    private String deviceId;
    private String messageReceived;

    @JsonProperty("created")
    public String getDateCreated() {
        return this.dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    @JsonProperty("modified")
    public String getDateModified() {
        return this.dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    @JsonProperty("rule.description")
    public String getRuleDescription() { return this.ruleDescription; }

    public void setRuleDescription(String ruleDescription) {
        this.ruleDescription = ruleDescription;
    }

    @JsonProperty("rule.severity")
    public String getRuleSeverity() {
        return this.ruleSeverity;
    }

    public void setRuleSeverity(String ruleSeverity) {
        this.ruleSeverity = ruleSeverity;
    }

    @JsonProperty("rule.id")
    public String getRuleId() {
        return this.ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    @JsonProperty("rule.actions")
    public List<IActionServiceModel> getActions() {
        return this.actions;
    }

    public void setActions(List<IActionServiceModel> actions) {
        this.actions = actions;
    }

    @JsonProperty("device.id")
    public String getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @JsonProperty("device.msg.received")
    public String getMessageReceived() {
        return this.messageReceived;
    }

    public void setMessageReceived(String messageReceived) { this.messageReceived = messageReceived; }
}
