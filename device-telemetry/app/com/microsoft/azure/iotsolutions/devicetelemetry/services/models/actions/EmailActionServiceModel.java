// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidInputException;
import play.libs.Json;

import javax.mail.internet.InternetAddress;
import java.util.*;

@JsonDeserialize(as = EmailActionServiceModel.class)
@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
public final class EmailActionServiceModel implements IActionServiceModel {

    private ActionType type;
    private Map<String, Object> parameters;

    private static final String SUBJECT = "Subject";
    private static final String NOTES = "Notes";
    private static final String RECIPIENTS = "Recipients";

    public EmailActionServiceModel(Map<String, Object> parameters) throws InvalidInputException {
        this.type = ActionType.Email;
        this.parameters = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.parameters.put(NOTES, "");

        // Ensure input is case insensitive
        Map<String,Object> parametersCaseInsensitive = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        parametersCaseInsensitive.putAll(parameters);

        if(!parametersCaseInsensitive.containsKey(SUBJECT)
            || !parametersCaseInsensitive.containsKey(RECIPIENTS)) {
            throw new InvalidInputException("Error converting recipient emails to list for action type 'Email'. " +
                "Recipient emails provided should be an array of valid email addresses " +
                "as strings.");
        } else {
            this.parameters.put(SUBJECT, parametersCaseInsensitive.get(SUBJECT));
            this.parameters.put(RECIPIENTS,
                this.validateAndConvertRecipientEmails(parametersCaseInsensitive.get(RECIPIENTS)));
        }

        if (parametersCaseInsensitive.containsKey(NOTES)) {
            this.parameters.put(NOTES, parametersCaseInsensitive.get(NOTES));
        }
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
    private List<String> validateAndConvertRecipientEmails(Object emails) throws InvalidInputException {
        List<String> emailList;
        try {
            emailList = Json.fromJson(Json.toJson(emails), List.class);
        } catch (Exception e) {
            throw new InvalidInputException("Error converting recipient emails to list for action type 'Email'. " +
                "Recipient emails provided should be an array of valid email addresses" +
                "as strings.");
        }

        if (emailList.size() == 0) {
            throw new InvalidInputException("Error, recipient email list for action type 'Email' is empty. " +
                "Please provide at least one valid email address.");
        }

        for (String email : emailList) {
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

        return emailList;
    }
}