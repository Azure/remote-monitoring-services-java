// Copyright (c) Microsoft. All rights reserved.

package actionsagent.test;

import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.actions.AlarmParser;
import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.models.AsaAlarmApiModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.ActionType;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.EmailActionServiceModel;
import org.junit.Test;
import org.junit.Assert;

import java.util.List;

public class AlarmParserTest {

    @Test
    public void CanParse_ProperlyFormattedJson() {
        // Arrange
        String data = "{\"created\":1539035437937,\"modified\":1539035437937,\"rule.description\":\"description\",\"rule.severity\":\"Warning\",\"rule.id\":\"TestRuleId\",\"rule.actions\":[{\"Type\":\"Email\",\"Parameters\":{\"Notes\":\"Test Note\",\"Subject\":\"Test Subject\",\"Recipients\":[\"sampleEmail1@gmail.com\"]}}],\"device.id\":\"Test Device Id\",\"device.msg.received\":1539035437937}\n" +
            "{\"created\":1539035437938,\"modified\":1539035437938,\"rule.description\":\"description1\",\"rule.severity\":\"Warning\",\"rule.id\":\"TestRuleId1\",\"rule.actions\":[{\"Type\":\"email\",\"Parameters\":{\"notes\":\"Test Note1\",\"subject\":\"Test Subject1\",\"recipients\":[\"sampleEmail1@gmail.com\", \"sampleEmail2@gmail.com\"]}}],\"device.id\":\"Test Device Id\",\"device.msg.received\":1539035437938}\n" +
            "{\"created\":1539035437940,\"modified\":1539035437940,\"rule.description\":\"description2\",\"rule.severity\":\"Info\",\"rule.id\":\"1234\",\"device.id\":\"Device Id\",\"device.msg.received\":1539035437940}";

        // Act
        List<AsaAlarmApiModel> result = AlarmParser.parseAlarmList(data);

        // Assert
        Assert.assertEquals(3, result.size());

        Assert.assertEquals("description", result.get(0).getRuleDescription());
        Assert.assertEquals("description1", result.get(1).getRuleDescription());
        Assert.assertEquals("description2", result.get(2).getRuleDescription());

        Assert.assertEquals(1, result.get(0).getActions().size());
        EmailActionServiceModel action = (EmailActionServiceModel) result.get(0).getActions().get(0);
        Assert.assertEquals(ActionType.Email, action.getType());
        List<String> recipients = action.getRecipients();
        Assert.assertEquals("Test Subject", action.getSubject());
        Assert.assertEquals("Test Note", action.getNotes());
        Assert.assertEquals(1, recipients.size());
        Assert.assertEquals("sampleEmail1@gmail.com", recipients.get(0));

        EmailActionServiceModel action2 = (EmailActionServiceModel) result.get(1).getActions().get(0);
        Assert.assertEquals("Test Subject1", action2.getSubject());
        Assert.assertEquals("Test Note1", action2.getNotes());
        List<String> recipients2 = action2.getRecipients();
        Assert.assertEquals(2, recipients2.size());
        Assert.assertEquals("sampleEmail1@gmail.com", recipients2.get(0));
        Assert.assertEquals("sampleEmail2@gmail.com", recipients2.get(1));

        Assert.assertEquals(null, result.get(2).getActions());
    }
}
