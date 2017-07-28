// Copyright (c) Microsoft. All rights reserved.

package webservice.test.v1.models;

import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.ConditionServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.*;
import helpers.UnitTest;
import org.joda.time.DateTime;
import org.junit.*;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class RuleApiModelTest {
    public RuleApiModel ruleApiModel = null;

    @Before
    public void setUp() {
        ArrayList<ConditionServiceModel> conditionArrayList = new ArrayList<>();
        ConditionServiceModel sampleCondition = null;
        conditionArrayList.add(sampleCondition);

        conditionArrayList.set(0, new ConditionServiceModel("test-value", "GreaterThan", "7"));

        ConditionListApiModel condtionList = new ConditionListApiModel(conditionArrayList);

        ArrayList<String> sampleEmails = new ArrayList<String>();
        sampleEmails.add("janedoe@contoso.com");
        sampleEmails.add("johndoe@contoso.com");

        ActionApiModel sampleAction = new ActionApiModel("email", "critical", sampleEmails, true, false);

        ruleApiModel = new RuleApiModel(
            "kkru1d1ouqahpmg",
            "5e503de7-0c57-4902-8654-dc82357360d1",
            "test-rule",
            new DateTime("2017-01-11T11:11:11-08:00"),
            new DateTime("2017-04-11T01:14:26-08:00"),
            false,
            "test description",
            "test-group-id",
            condtionList,
            sampleAction);
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
