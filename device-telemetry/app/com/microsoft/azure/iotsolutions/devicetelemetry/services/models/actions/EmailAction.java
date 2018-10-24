// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidInputException;
import play.libs.Json;

import javax.mail.internet.InternetAddress;
import java.util.*;

@JsonDeserialize(as = EmailAction.class)
@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
public final class EmailAction implements IAction {

    private ActionType type;
    private Map<String, Object> parameters;

    private static final String SUBJECT = "Subject";
    private static final String NOTES = "Notes";
    private static final String RECIPIENTS = "Recipients";

    public EmailAction() {
        this.type = ActionType.Email;
        this.parameters = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER) {{
            put(NOTES, "");
        }};
    }

    public EmailAction(Map<String, Object> parameters) throws InvalidInputException {
        this(ActionType.Email, parameters);
    }

    public EmailAction(ActionType type, Map<String, Object> parameters) throws InvalidInputException {
        this.type = type;
        this.parameters = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        if (!(parameters.containsKey(SUBJECT) && parameters.containsKey(RECIPIENTS))) {
            throw new InvalidInputException(String.format("Error, missing parameter for email action." +
                " Required fields are: '{%s}' and '{%s}'.", SUBJECT, RECIPIENTS));
        }

        if (parameters.containsKey(NOTES)) {
            this.parameters.put(NOTES, parameters.get(NOTES));
        }

        this.parameters.put(SUBJECT, parameters.get(SUBJECT));
        this.parameters.put(RECIPIENTS, validatAndConvertRecipientEmails(parameters.get(RECIPIENTS)));
    }

    public ActionType getType() {
        return type;
    }

    public void setType(ActionType Type) {
        this.type = Type;
    }

    public Map<String, Object> getParameters() {
        return this.parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public String getNotes() {
        if (this.parameters.containsKey(NOTES)) {
            return this.parameters.get(NOTES).toString();
        }

        return "";
    }

    public String getSubject() {
        return this.parameters.get(SUBJECT).toString();
    }

    public List<String> getRecipients() {
        return (List<String>) this.parameters.get(RECIPIENTS);
    }

    /**
     * Validates recipient email addresses and converts to a list of email strings
     *
     * @param emails an object include a list of emails
     * @return a list of validated email address
     */
    private List<String> validatAndConvertRecipientEmails(Object emails) throws InvalidInputException {
        List<String> result;
        try {
            result = Json.fromJson(Json.toJson(emails), List.class);
        } catch (Exception e) {
            throw new InvalidInputException("Error converting recipient emails to list for action type 'Email'. " +
                "Recipient emails provided should be an array of valid email addresses" +
                "as strings.");
        }

        if (result.size() == 0) {
            throw new InvalidInputException("Error, recipient email list for action type 'Email' is empty. " +
                "Please provide at least one valid email address.");
        }

        for (String email : result) {
            try {
                InternetAddress mail = new InternetAddress(email);
                mail.validate();
            } catch (Exception e) {
                throw new InvalidInputException("Error with recipient email format for action type 'Email'." +
                    "Invalid email provided. Please ensure at least one recipient " +
                    "email address is provided and that all recipient email addresses " +
                    "are valid.");
            }
        }

        return result;
    }
}