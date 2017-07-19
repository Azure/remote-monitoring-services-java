// Copyright (c) Microsoft. All rights reserved.

package webservice.test.v1.controllers;

import com.microsoft.azure.iotsolutions.devicetelemetry.services.Messages;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.controllers.MessagesController;
import helpers.UnitTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import play.mvc.Result;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

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
    public void provideMessageListResult() {
        // TODO Mock the MessagesController dependency
        MessagesController controller = new MessagesController(new Messages());

        Result result = controller.list();

        assertThat(result.body().isKnownEmpty(), is(false));
    }
}
