package com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions;

import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.exceptions.BadRequestException;
import sun.awt.image.BadDepthException;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.*;

public class EmailActionServiceModel implements IActionServiceModel {

    private ActionType type;
    private Map<String, Object> parameters;

    private static final String SUBJECT = "Subject";
    private static final String NOTES = "Notes";
    private static final String RECIPIENTS = "Recipients";

    public EmailActionServiceModel() {
        this.type = ActionType.Email;
        this.parameters = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    public EmailActionServiceModel(Map<String, Object> parameters) throws BadRequestException {
        this.type = ActionType.Email;
        this.parameters = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.parameters.put(NOTES, "");

        Map<String,Object> parametersCaseInsensitive = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        parametersCaseInsensitive.putAll(parameters);

        if(!parametersCaseInsensitive.containsKey(SUBJECT)
            || !parametersCaseInsensitive.containsKey(RECIPIENTS)) {
            throw new BadRequestException("Error converting recipient emails to list for action type 'Email'. " +
                    "Recipient emails provided should be an array of valid email addresses" +
                    "as strings.");
        }

        if (parameters.containsKey(NOTES)) {
            this.parameters.put(NOTES, parametersCaseInsensitive.get(NOTES));
        }

        this.parameters.put(SUBJECT, parametersCaseInsensitive.get(SUBJECT));
        this.parameters.put(RECIPIENTS,
                this.ValidateAndConvertRecipientEmails(parametersCaseInsensitive.get(RECIPIENTS)));
    }

    @Override
    public ActionType getType() {
        return this.type;
    }

    @Override
    public Map<String, Object> getParameters() {
        return this.parameters;
    }

    private List<String> ValidateAndConvertRecipientEmails(Object emails) throws BadRequestException {
        List<String> result;
        try {
            result = (ArrayList<String>) emails;
        } catch (Exception e) {
            throw new BadRequestException("Error converting recipient emails to list for action type 'Email'. " +
                    "Recipient emails provided should be an array of valid email addresses" +
                    "as strings.");
        }

        if (result.size() == 0) {
            throw new BadRequestException("Error, recipient email list for action type 'Email' is empty. " +
                    "Please provide at least one valid email address.");
        }

        for (String email: result) {
            try {
                InternetAddress mail = new InternetAddress(email);
                mail.validate();
            } catch (AddressException e) {
                throw new BadRequestException("Error with recipient email format for action type 'Email'." +
                        "Invalid email provided. Please ensure at least one recipient " +
                        "email address is provided and that all recipient email addresses " +
                        "are valid.");
            }
        }
        return result;
    }
}
