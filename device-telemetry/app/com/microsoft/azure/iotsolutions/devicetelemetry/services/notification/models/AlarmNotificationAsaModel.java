// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AlarmNotificationAsaModel {
    @JsonProperty("created")
    private String dateCreated;

    @JsonProperty("modified")
    private String dateModified;

    @JsonProperty("rule.description")
    private String ruleDescription;

    @JsonProperty("rule.severity")
    private String ruleSeverity;

    @JsonProperty("rule.id")
    private String ruleId;

    @JsonProperty("rule.actions")
    private List<ActionAsaModel> actions;

    @JsonProperty("device.id")
    private String deviceId;

    @JsonProperty("device.msg.received")
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
    public List<ActionAsaModel> getActions() {
        return this.actions;
    }

    public void setActions(List<ActionAsaModel> actions) {
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
