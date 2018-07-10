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
        type = IActionServiceModel.Type.Email;
        subject = "";
        body = "";
        email = new ArrayList<>();
    }

    public EmailServiceModel(IActionServiceModel.Type type, Map<String, Object> parameters) throws InvalidInputException {
        this();
        this.type = type;
        if (parameters.containsKey(SUBJECT)) {
            subject = (String) parameters.get(SUBJECT);
        }
        if (parameters.containsKey(TEMPLATE)) {
            body = (String) parameters.get(TEMPLATE);
        }

        email = (ArrayList<String>) parameters.get(EMAIL);

        if (!this.isValid()) {
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

    private Boolean isValid() throws InvalidInputException {
        try {
            if (this.email == null) {
                throw new InvalidInputException(EMPTY_EMAIL);
            }
            for (String email : email) {
                try {
                    InternetAddress mail = new InternetAddress(email);
                    mail.validate();
                } catch (AddressException e) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}