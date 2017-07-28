// Copyright (c) Microsoft. All rights reserved.

package webservice.test.v1.controllers;

import com.microsoft.azure.iotsolutions.devicetelemetry.services.IMessages;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.MessageListServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.MessageServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.controllers.MessagesController;
import helpers.UnitTest;
import org.joda.time.DateTime;
import org.junit.*;
import org.junit.experimental.categories.Category;
import play.mvc.Result;

import java.util.ArrayList;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MessagesControllerTest {
    @Before
    public void setUp() {
        // something before every test
    }

    @After
    public void tearDown() {
        // something after every test
    }

    @Test(timeout = 5000)
    @Category({UnitTest.class})
    public void itGetsAllMessages() throws InvalidInputException {

        // Arrange
        IMessages messages = mock(IMessages.class);
        MessagesController controller = new MessagesController(mock(IMessages.class));
        ArrayList<MessageServiceModel> msgs = new ArrayList<MessageServiceModel>() {{
            add(new MessageServiceModel());
            add(new MessageServiceModel());
        }};
        MessageListServiceModel res = new MessageListServiceModel(msgs, null);
        when(messages.getList(
            DateTime.now(), DateTime.now(), "asc", 0, 100, new String[0]))
            .thenReturn(res);

        // Act
        Result response = controller.list(null, null, null, null, null, null);

        // Assert
        assertThat(response.body().isKnownEmpty(), is(false));
    }
}
