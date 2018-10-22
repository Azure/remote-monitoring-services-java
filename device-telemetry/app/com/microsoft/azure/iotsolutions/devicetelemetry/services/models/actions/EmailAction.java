// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidInputException;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.*;

@JsonDeserialize(as = EmailAction.class)
@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
public final class EmailAction implements IAction {

    private ActionType type;
    private String subject;
    private String body;
    private List<String> recipients;
    private static final String SUBJECT = "Subject";
    private static final String NOTES = "Notes";
    private static final String RECIPIENTS = "Recipients";
    private static final String IMPROPER_EMAIL = "Improperly formatted email";
    private static final String EMPTY_EMAIL = "Empty email list provided for actionType email";

    public EmailAction() {
        this.type = ActionType.Email;
        this.subject = "";
        this.body = "";
        this.recipients = new ArrayList<>();
    }

    public EmailAction(Map<String, Object> parameters) throws InvalidInputException {
        this(ActionType.Email, parameters);
    }

    public EmailAction(ActionType type, Map<String, Object> parameters) throws InvalidInputException {
        this.type = type;
        if (parameters.containsKey(SUBJECT)) {
            this.subject = (String) parameters.get(SUBJECT);
        }
        if (parameters.containsKey(NOTES)) {
            this.body = (String) parameters.get(NOTES);
        }

        this.recipients = (ArrayList<String>) parameters.get(RECIPIENTS);

        try {
            this.isValid();
        } catch (Exception e) {
            throw new InvalidInputException(IMPROPER_EMAIL);
        }
    }

    public ActionType getType() {
        return type;
    }

    public void setType(ActionType Type) {
        this.type = Type;
    }

    public Map<String, Object> getParameters() {
        Map<String, Object> result = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);;
        result.put(SUBJECT, subject);
        result.put(NOTES, body);
        result.put(RECIPIENTS, recipients);
        return result;
    }

    private void isValid() throws InvalidInputException, AddressException {
        // checks for invalid email formatting or null emails
        if (this.recipients == null) {
            throw new InvalidInputException(EMPTY_EMAIL);
        }
        for (String email : recipients) {
            InternetAddress mail = new InternetAddress(email);
            mail.validate();
        }
    }
}