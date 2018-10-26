// Copyright (c) Microsoft. All rights reserved.

package services.test;

import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.EmailActionServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.IActionServiceModel;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ActionTest {
    private static final String PARAM_NOTES = "Chiller pressure is at 250 which is high";
    private static final String PARAM_SUBJECT = "Alert Notification";
    private static final String PARAM_RECIPIENTS = "sampleEmail@gmail.com";
    private static final String PARAM_NOTES_KEY = "Notes";
    private static final String PARAM_SUBJECT_KEY = "Subject";
    private static final String PARAM_RECIPIENTS_KEY = "Recipients";

    @Test
    public void Should_ReturnActionModel_WhenValid() throws InvalidInputException {
        //Arrange
        Map<String, Object> parameters = this.createDefaultParameters();

        // Act
        IActionServiceModel action = new EmailActionServiceModel(parameters);

        // Assert
        Assert.assertEquals(IActionServiceModel.ActionType.Email, action.getType());
        Assert.assertEquals(PARAM_NOTES, action.getParameters().get(PARAM_NOTES_KEY));
        Assert.assertEquals(PARAM_SUBJECT, action.getParameters().get(PARAM_SUBJECT_KEY));
        List<String> recipients = (List<String>)action.getParameters().get(PARAM_RECIPIENTS_KEY);
        Assert.assertEquals(1, recipients.size());
        Assert.assertEquals(PARAM_RECIPIENTS, recipients.get(0));
    }

    @Test
    public void Should_ReturnActionModel_WhenValidCaseInsensitive() throws InvalidInputException {
        //Arrange
        Map<String, Object> parameters = this.createDefaultParameters();
        parameters.put("SUBjEct", PARAM_SUBJECT);

        // Act
        IActionServiceModel action = new EmailActionServiceModel(parameters);

        // Assert
        Assert.assertEquals(IActionServiceModel.ActionType.Email, action.getType());
        Assert.assertEquals(PARAM_NOTES, action.getParameters().get(PARAM_NOTES_KEY));
        Assert.assertEquals(PARAM_SUBJECT, action.getParameters().get(PARAM_SUBJECT_KEY));
        List<String> recipients = (List<String>)action.getParameters().get(PARAM_RECIPIENTS_KEY);
        Assert.assertEquals(1, recipients.size());
        Assert.assertEquals(PARAM_RECIPIENTS, recipients.get(0));
    }

    @Test
    public void Should_ReturnActionModel_WhenNotesAreMissing() throws InvalidInputException {
        //Arrange
        ArrayList<String> emailList = new ArrayList<>();
        emailList.add(PARAM_RECIPIENTS);

        Map<String, Object> parameters = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        parameters.put(PARAM_SUBJECT_KEY, PARAM_SUBJECT);
        parameters.put(PARAM_RECIPIENTS_KEY, emailList);

        // Act
        IActionServiceModel action = new EmailActionServiceModel(parameters);

        // Assert
        Assert.assertEquals(IActionServiceModel.ActionType.Email, action.getType());
        Assert.assertEquals("", action.getParameters().get(PARAM_NOTES_KEY));
        Assert.assertEquals(PARAM_SUBJECT, action.getParameters().get(PARAM_SUBJECT_KEY));
        List<String> recipients = (List<String>)action.getParameters().get(PARAM_RECIPIENTS_KEY);
        Assert.assertEquals(1, recipients.size());
        Assert.assertEquals(PARAM_RECIPIENTS, recipients.get(0));
    }

    @Test(expected = InvalidInputException.class)
    public void Should_ThrowInvalidInputException_WhenEmailInvalid() throws InvalidInputException {
        // Arrange
        Map<String, Object> parameters = this.createDefaultParameters();
        ArrayList<String> emailList = new ArrayList<>();
        emailList.add("sampleEmailgmail.com");
        parameters.put(PARAM_RECIPIENTS_KEY, emailList);

        // Act--should throw exception
        IActionServiceModel action = new EmailActionServiceModel(parameters);
    }

    @Test(expected = InvalidInputException.class)
    public void Should_ThrowInvalidInputException_WhenNoRecipients() throws InvalidInputException {
        // Arrange
        Map<String, Object> parameters = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        parameters.put(PARAM_NOTES_KEY, PARAM_NOTES);
        parameters.put(PARAM_SUBJECT_KEY, PARAM_SUBJECT);

        // Act--should throw exception
        IActionServiceModel action = new EmailActionServiceModel(parameters);
    }

    @Test(expected = InvalidInputException.class)
    public void Should_ThrowInvalidInputException_EmailIsNotArray() throws InvalidInputException {
        // Arrange
        Map<String, Object> parameters = this.createDefaultParameters();
        parameters.put(PARAM_RECIPIENTS_KEY, PARAM_RECIPIENTS);

        // Act--should throw exception
        IActionServiceModel action = new EmailActionServiceModel(parameters);
    }

    private Map<String, Object> createDefaultParameters() {
        ArrayList<String> emailList = new ArrayList<>();
        emailList.add(PARAM_RECIPIENTS);

        Map<String, Object> parameters = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        parameters.put(PARAM_NOTES_KEY, PARAM_NOTES);
        parameters.put(PARAM_SUBJECT_KEY, PARAM_SUBJECT);
        parameters.put(PARAM_RECIPIENTS_KEY, emailList);

        return parameters;
    }
}
