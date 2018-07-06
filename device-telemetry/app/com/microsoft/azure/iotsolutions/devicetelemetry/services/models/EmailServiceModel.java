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

    private Type Type;
    private String Subject;
    private String Body;
    private List<String> Email;

    public EmailServiceModel() {
        Type = IActionServiceModel.Type.Email;
        Subject = "";
        Body = "";
        Email = new ArrayList<>();
    }

    public Type getType() {
        return Type;
    }

    public void setType(Type Type) {
        this.Type = Type;
    }

    public EmailServiceModel(IActionServiceModel.Type type, Map<String, Object> parameters) throws InvalidInputException {
        this();
        Type = type;
        if (parameters.containsKey("Subject")) {
            Subject = (String) parameters.get("Subject");
        }
        if (parameters.containsKey("Template")) {
            Body = (String) parameters.get("Template");
        }

        Email = (ArrayList<String>) parameters.get("Email");

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