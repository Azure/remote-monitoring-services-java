package com.microsoft.azure.iotsolutions.devicetelemetry.services.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidInputException;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class EmailServiceModel implements IActionServiceModel {

    private Type ActionType = Type.Email;
    public String Subject = "";
    public String Body = "";
    public List<String> Email = new ArrayList<>();

    public EmailServiceModel() {
        //empty constructor
    }

    public Type getActionType() {
        return ActionType;
    }

    public void setActionType(Type actionType) {
        ActionType = actionType;
    }

    public EmailServiceModel(Type type, Map<String, Object> parameters) throws InvalidInputException {
        ActionType = type;
        if (parameters.containsKey("Subject")) {
            Subject = (String) parameters.get("Subject");
        }
        if (parameters.containsKey("Template")) {
            Body = (String) parameters.get("Template");
        }

        try {
            if (parameters.containsKey("Email")) {
                try {
                    Email = (ArrayList<String>) parameters.get("Email");
                } catch (ClassCastException e) {
                    throw new InvalidInputException("Email field is a list of string");
                }
            }
        } catch (Exception e) {
            throw new InvalidInputException("Invalid input");
        }

        if (!isValid()) {
            throw new InvalidInputException("Improperly formatted email");
        }
    }

    public Map<String, Object> getParameters() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("Subject", Subject);
        ret.put("Template", Body);
        ret.put("Email", Email);
        return ret;
    }

    private Boolean isValid() throws InvalidInputException {
        try {
            if (Email == null) {
                throw new InvalidInputException("Empty email list provided for actionType email");
            }
            for (String email : Email) {
                try {
                    InternetAddress mail = new InternetAddress(email);
                    mail.validate();
                } catch (AddressException e) {
                    return false;
                }
            }
        } catch (Exception e) {
            throw new InvalidInputException("Improperly formatted email");
        }
        return true;
    }
}