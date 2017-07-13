// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.controllers;

import com.microsoft.azure.iotsolutions.devicetelemetry.services.Rules;
import helpers.UnitTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import play.mvc.Result;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class RulesControllerTest {
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
    public void provideRuleListResult() {
        // TODO Mock the RulesController Dependency
        RulesController controller = new RulesController(new Rules());

        Result result = controller.list();

        assertThat(result.body().isKnownEmpty(), is(false));
    }
}
