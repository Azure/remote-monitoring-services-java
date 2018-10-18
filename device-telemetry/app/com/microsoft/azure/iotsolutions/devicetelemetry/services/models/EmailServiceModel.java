// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidInputException;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonDeserialize(as = EmailServiceModel.class)
@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
public final class EmailServiceModel implements IActionServiceModel {

    private Type type;
    private String subject;
    private String body;
    private List<String> email;
    private static final String SUBJECT = "Subject";
    private static final String TEMPLATE = "Template";
    private static final String EMAIL = "Email";
    private static final String IMPROPER_EMAIL = "Improperly formatted email";
    private static final String EMPTY_EMAIL = "Empty email list provided for actionType email";

    public EmailServiceModel() {
        this.type = IActionServiceModel.Type.Email;
        this.subject = "";
        this.body = "";
        this.email = new ArrayList<>();
    }

    public EmailServiceModel(IActionServiceModel.Type type, Map<String, Object> parameters) throws InvalidInputException {
        this();
        this.type = type;
        if (parameters.containsKey(SUBJECT)) {
            this.subject = (String) parameters.get(SUBJECT);
        }
        if (parameters.containsKey(TEMPLATE)) {
            this.body = (String) parameters.get(TEMPLATE);
        }

        this.email = (ArrayList<String>) parameters.get(EMAIL);

        try {
            this.isValid();
        } catch (Exception e) {
            throw new InvalidInputException(IMPROPER_EMAIL);
        }
    }

    public Type getType() {
        return type;
    }

    public void setType(Type Type) {
        this.type = Type;
    }

    public Map<String, Object> getParameters() {
        Map<String, Object> result = new HashMap<>();
        result.put(SUBJECT, subject);
        result.put(TEMPLATE, body);
        result.put(EMAIL, email);
        return result;
    }

    private void isValid() throws InvalidInputException, AddressException {
        // checks for invalid email formatting or null emails
        if (this.email == null) {
            throw new InvalidInputException(EMPTY_EMAIL);
        }
        for (String email : email) {
            InternetAddress mail = new InternetAddress(email);
            mail.validate();
        }
    }
}