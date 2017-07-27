// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.ActionServiceModel;

import java.util.ArrayList;

/**
 * Public model used by the web service.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ActionApiModel {

    private String type = null;
    private String severity = null;
    private ArrayList<String> customEmails = null;
    private Boolean sendToServiceAdmin = false;
    private Boolean sendToServiceCoAdmins = false;

    /**
     * Create an instance given the property values.
     *
     * @param type
     * @param severity
     * @param customEmails
     * @param sendToServiceAdmin
     * @param sendToServiceCoAdmins
     */
    public ActionApiModel(
        final String type,
        final String severity,
        final ArrayList<String> customEmails,
        final Boolean sendToServiceAdmin,
        final Boolean sendToServiceCoAdmins) {
        this.type = type;
        this.severity = severity;
        this.customEmails = customEmails;
        this.sendToServiceAdmin = sendToServiceAdmin;
        this.sendToServiceCoAdmins = sendToServiceCoAdmins;
    }

    /**
     * Create instance given a service model.
     *
     * @param action service model
     */
    public ActionApiModel(final ActionServiceModel action) {
        if (action == null) return;

        this.type = action.getType();
        this.severity = action.getSeverity();
        this.customEmails = action.getCustomEmails();
        this.sendToServiceAdmin = action.getSendToServiceAdmin();
        this.sendToServiceCoAdmins = action.getSendToServiceCoAdmins();
    }

    @JsonProperty("Type")
    public String getType() {
        return this.type;
    }

    @JsonProperty("Severity")
    public String getSeverity() {
        return this.severity;
    }

    @JsonProperty("CustomEmails")
    public ArrayList<String> getCustomEmails() {
        return this.customEmails;
    }

    @JsonProperty("SendToServiceAdmin")
    public Boolean getSendToServiceAdmin() {
        return this.sendToServiceAdmin;
    }

    @JsonProperty("SendToServiceCoAdmins")
    public Boolean getSendToServiceCoAdmins() {
        return this.sendToServiceCoAdmins;
    }
}
