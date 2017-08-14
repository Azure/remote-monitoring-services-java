// Copyright (c) Microsoft. All rights reserved.

package webservice.test.v1.controllers;

import com.microsoft.azure.iotsolutions.devicetelemetry.services.IRules;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.RuleServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.controllers.RulesController;
import helpers.UnitTest;
import org.eclipse.jetty.util.Callback;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;
import java.util.concurrent.CompletionStage;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    public void provideRulesList() {

        // Arrange
        CompletionStage<List<RuleServiceModel>> ruleListResult =
            Callback.Completable.completedFuture(null);

        IRules rules = mock(IRules.class);
        RulesController controller = new RulesController(rules);
        when(rules.getListAsync(
            "asc",
            0,
            1000,
            null))
            .thenReturn(ruleListResult);

        // Act
        controller.listAsync(
            "asc",
            0,
            1000,
            null)
            .thenApply(response -> {
                // Assert
                assertThat(response.body().isKnownEmpty(), is(false));
                return null;
            });
    }

    @Test(timeout = 5000)
    @Category({UnitTest.class})
    public void provideRuleById() {

        // Arrange
        CompletionStage<RuleServiceModel> ruleResult =
            Callback.Completable.completedFuture(null);

        IRules rules = mock(IRules.class);
        RulesController controller = new RulesController(rules);
        when(rules.getAsync(
            "1")).thenReturn(ruleResult);

        // Act
        controller.getAsync("1").thenApply(response -> {
            // Assert
            assertThat(response.body().isKnownEmpty(), is(false));
            return null;
        });
    }

    @Test(timeout = 5000)
    @Category({UnitTest.class})
    public void deleteRule() {

        // Arrange
        CompletionStage<Boolean> result =
            Callback.Completable.completedFuture(null);

        IRules rules = mock(IRules.class);
        RulesController controller = new RulesController(rules);
        when(rules.deleteAsync(
            "1")).thenReturn(result);

        // Act
        controller.deleteAsync("1").thenApply(response -> {
            // Assert
            assertThat(response.body().isKnownEmpty(), is(false));
            return null;
        });
    }
}
