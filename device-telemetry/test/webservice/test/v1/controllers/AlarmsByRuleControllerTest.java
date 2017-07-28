// Copyright (c) Microsoft. All rights reserved.

package webservice.test.v1.controllers;

import com.microsoft.azure.iotsolutions.devicetelemetry.services.AlarmsByRule;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.controllers.AlarmsByRuleController;
import helpers.UnitTest;
import org.junit.*;
import org.junit.experimental.categories.Category;
import play.mvc.Result;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AlarmsByRuleControllerTest {
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
    public void provideAlarmsByRuleByIdResult() {
        // TODO Mock the MessagesController dependency
        AlarmsByRuleController controller = new AlarmsByRuleController(new AlarmsByRule());

        Result result = controller.get("1234", null, null, "asc", 0, 0, null);

        assertThat(result.body().isKnownEmpty(), is(false));
    }

    @Test(timeout = 5000)
    @Category({UnitTest.class})
    public void provideAlarmsByRuleListResult() {
        // TODO Mock the MessagesController dependency
        AlarmsByRuleController controller = new AlarmsByRuleController(new AlarmsByRule());

        Result result = controller.list(null, null, "asc", 0, 0, null);

        assertThat(result.body().isKnownEmpty(), is(false));
    }
}
