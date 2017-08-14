// Copyright (c) Microsoft. All rights reserved.

package webservice.test.v1.models;

import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.ConditionApiModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.RuleApiModel;
import helpers.UnitTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class RuleApiModelTest {
    public RuleApiModel ruleApiModel = null;

    @Before
    public void setUp() {
        ArrayList<ConditionApiModel> conditionList = new ArrayList<>();

        conditionList.add(new ConditionApiModel(
            "test-value",
            "GreaterThan",
            "7"));

        ruleApiModel = new RuleApiModel(
            "kkru1d1ouqahpmg",
            "5e503de7-0c57-4902-8654-dc82357360d1",
            "test-rule",
            "2017-01-11T11:11:11-08:00",
            "2017-04-11T01:14:26-08:00",
            false,
            "test description",
            "test-group",
            "warning",
            conditionList);
    }

    @After
    public void tearDown() {
        // something after every test
    }

    @Test(timeout = 5000)
    @Category({UnitTest.class})
    public void createRuleApiModel() {
        // required values
        assertThat(ruleApiModel.getETag().isEmpty(), is(false));
        assertThat(ruleApiModel.getId().isEmpty(), is(false));
        assertThat(ruleApiModel.getDateCreated().isEmpty(), is(false));
        assertThat(ruleApiModel.getDateModified().isEmpty(), is(false));
    }
}
