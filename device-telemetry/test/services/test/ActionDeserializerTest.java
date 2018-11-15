// Copyright (c) Microsoft. All rights reserved.

package services.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.ActionType;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.IActionServiceModel;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class ActionDeserializerTest {

    private static final String PARAM_NOTES = "Chiller pressure is at 250 which is high";
    private static final String PARAM_SUBJECT = "Alert Notification";
    private static final String PARAM_RECIPIENTS = "sampleEmail@gmail.com";
    private static final String PARAM_NOTES_KEY = "Notes";
    private static final String PARAM_SUBJECT_KEY = "Subject";
    private static final String PARAM_RECIPIENTS_KEY = "Recipients";

    @Test
    public void ReturnsEmailAction_WhenEmailActionJsonPassed() throws IOException {
        // Arrange
        String testString = "{\"Type\":\"Email\"," +
                "\"Parameters\":{\"Notes\":\"" + PARAM_NOTES +
                "\",\"Subject\":\"" + PARAM_SUBJECT +
                "\",\"Recipients\":[\"" + PARAM_RECIPIENTS + "\"]}}";

        ObjectMapper mapper = new ObjectMapper();

        // Act
        IActionServiceModel serviceModel = mapper.readValue(testString, IActionServiceModel.class);

        // Assert
        Assert.assertEquals(ActionType.Email, serviceModel.getType());
        Assert.assertNotNull(serviceModel.getParameters());
        Assert.assertEquals(PARAM_NOTES, serviceModel.getParameters().get(PARAM_NOTES_KEY));
        Assert.assertEquals(PARAM_SUBJECT, serviceModel.getParameters().get(PARAM_SUBJECT_KEY));
        List<String> recipients = (List<String>)serviceModel.getParameters().get(PARAM_RECIPIENTS_KEY);
        Assert.assertEquals(1, recipients.size());
        Assert.assertEquals(PARAM_RECIPIENTS, recipients.get(0));
    }
}
