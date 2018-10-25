// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidInputException;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.*;

@JsonDeserialize(as = EmailActionServiceModel.class)
@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
public class EmailActionServiceModel implements IActionServiceModel {

    private ActionType type;

    // Parameters should always be case-insensitive
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
        }

        if (parameters.containsKey(NOTES)) {
            this.parameters.put(NOTES, parametersCaseInsensitive.get(NOTES));
        }

        this.parameters.put(SUBJECT, parametersCaseInsensitive.get(SUBJECT));
        this.parameters.put(RECIPIENTS,
                this.validateAndConvertRecipientEmails(parametersCaseInsensitive.get(RECIPIENTS)));
    }

    @Override
    @JsonProperty("Type")
    public ActionType getType() {
        return this.type;
    }

    @Override
    @JsonProperty("Parameters")
    public Map<String, Object> getParameters() {
        return this.parameters;
    }

    // Validates recipient email addresses and converts to a list of email strings
    private List<String> validateAndConvertRecipientEmails(Object emails) throws InvalidInputException {
        List<String> results;
        try {
            results = (ArrayList<String>) emails;
        } catch (Exception e) {
            throw new InvalidInputException("Error converting recipient emails to list for action type 'Email'. " +
                    "Recipient emails provided should be an array of valid email addresses " +
                    "as strings.");
        }

        if (results.size() == 0) {
            throw new InvalidInputException("Error, recipient email list for action type 'Email' is empty. " +
                    "Please provide at least one valid email address.");
        }

        for (String email: results) {
            try {
                InternetAddress mail = new InternetAddress(email);
                mail.validate();
            } catch (AddressException e) {
                throw new InvalidInputException("Error with recipient email format for action type 'Email'." +
                        "Invalid email provided. Please ensure at least one recipient " +
                        "email address is provided and that all recipient email addresses " +
                        "are valid.");
            }
        }

        return results;
    }
}
