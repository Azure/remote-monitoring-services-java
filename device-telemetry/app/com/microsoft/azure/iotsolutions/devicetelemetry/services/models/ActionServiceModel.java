// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.models;

import java.util.ArrayList;

/*
 * Specifies what action to take when a rule triggers an alarm.
 */
public final class ActionServiceModel {

    private final String type;
    private final String severity;
    private final ArrayList<String> customEmails;
    private final Boolean sendToServiceAdmin;
    private final Boolean sendToServiceCoAdmins;

    public ActionServiceModel(
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

    public String getType() {
        return this.type;
    }

    public String getSeverity() {
        return this.severity;
    }

    public ArrayList<String> getCustomEmails() {
        return this.customEmails;
    }

    public Boolean getSendToServiceAdmin() {
        return this.sendToServiceAdmin;
    }

    public Boolean getSendToServiceCoAdmins() {
        return this.sendToServiceCoAdmins;
    }
}
