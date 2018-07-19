package com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AlarmNotificationAsaModel {
    @JsonProperty("created")
    private String DateCreated;

    @JsonProperty("modified")
    private String DateModified;

    @JsonProperty("rule.description")
    private String Rule_description;

    @JsonProperty("rule.severity")
    private String Rule_severity;

    @JsonProperty("rule.id")
    private String Rule_id;

    @JsonProperty("rule.actions")
    private List<ActionAsaModel> Actions;

    @JsonProperty("device.id")
    private String Device_id;

    @JsonProperty("device.msg.received")
    private String Message_received;

    @JsonProperty("created")
    public String getDateCreated() {
        return DateCreated;
    }

    public void setDateCreated(String dateCreated) {
        DateCreated = dateCreated;
    }

    @JsonProperty("modified")
    public String getDateModified() {
        return DateModified;
    }

    public void setDateModified(String dateModified) {
        DateModified = dateModified;
    }

    @JsonProperty("rule.description")
    public String getRule_description() {
        return Rule_description;
    }

    public void setRule_description(String rule_description) {
        Rule_description = rule_description;
    }

    @JsonProperty("rule.severity")
    public String getRule_severity() {
        return Rule_severity;
    }

    public void setRule_severity(String rule_severity) {
        Rule_severity = rule_severity;
    }

    @JsonProperty("rule.id")
    public String getRule_id() {
        return Rule_id;
    }

    public void setRule_id(String rule_id) {
        Rule_id = rule_id;
    }

    @JsonProperty("rule.actions")
    public List<ActionAsaModel> getActions() {
        return Actions;
    }

    public void setActions(List<ActionAsaModel> actions) {
        Actions = actions;
    }

    @JsonProperty("device.id")
    public String getDevice_id() {
        return Device_id;
    }

    public void setDevice_id(String device_id) {
        Device_id = device_id;
    }

    @JsonProperty("device.msg.received")
    public String getMessage_received() {
        return Message_received;
    }

    public void setMessage_received(String message_received) {
        Message_received = message_received;
    }

    public AlarmNotificationAsaModel(){
        // empty constructor
    }
}
