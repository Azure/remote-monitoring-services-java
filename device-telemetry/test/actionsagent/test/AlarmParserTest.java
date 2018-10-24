// Copyright (c) Microsoft. All rights reserved.

package actionsagent.test;

import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.actions.AlarmParser;
import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.models.AsaAlarmApiModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.ActionType;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.EmailAction;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.IAction;
import org.junit.Test;
import org.junit.Assert;
import java.util.List;

public class AlarmParserTest {

    @Test
    public void CanParse_ProperlyFormattedJson() {
        // Arrange
        String data = "{\"created\":1539035437937,\"modified\":1539035437937,\"rule.description\":\"description\",\"rule.severity\":\"Warning\",\"rule.id\":\"TestRuleId\",\"rule.actions\":[{\"Type\":\"Email\",\"Parameters\":{\"Notes\":\"Test Note\",\"Subject\":\"Test Subject\",\"Recipients\":[\"sampleEmail@gmail.com\"]}}],\"device.id\":\"Test Device Id\",\"device.msg.received\":1539035437937}\n" +
            "{\"created\":1539035437940,\"modified\":1539035437940,\"rule.description\":\"description2\",\"rule.severity\":\"Info\",\"rule.id\":\"1234\",\"device.id\":\"Device Id\",\"device.msg.received\":1539035437940}";

        // Act
        List<AsaAlarmApiModel> result = AlarmParser.parseAlarmList(data);

        // Assert
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("description", result.get(0).getRuleDescription());
        Assert.assertEquals("description2", result.get(1).getRuleDescription());
        Assert.assertEquals(1, result.get(0).getActions().size());
        IAction action = result.get(0).getActions().get(0);
        Assert.assertEquals(ActionType.Email, action.getType());
        List<String> recipients = ((EmailAction)action).getRecipients();
        Assert.assertEquals(1, recipients.size());
        Assert.assertEquals("sampleEmail@gmail.com", recipients.get(0));
    }
}
