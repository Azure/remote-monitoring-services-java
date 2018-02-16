// Copyright (c) Microsoft. All rights reserved.

package webservice.test.v1.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.IRules;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.ConditionServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.RuleServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.controllers.RulesController;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.RuleApiModel;
import helpers.UnitTest;
import org.eclipse.jetty.util.Callback;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import play.libs.Json;
import play.mvc.Http;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static play.libs.Json.toJson;

public class RulesControllerTest {

    private RuleServiceModel sampleNewRuleServiceModel;

    @Before
    public void setUp() {
        // something before every test

        ConditionServiceModel sampleCondition = new ConditionServiceModel(
            "TestField",
            "Equals",
            "TestValue"
        );
        ArrayList<ConditionServiceModel> sampleConditions = new ArrayList<>();
        sampleConditions.add(sampleCondition);

        this.sampleNewRuleServiceModel = new RuleServiceModel(
            "TestName",
            true,
            "Test Description",
            "TestGroup",
            "critical",
            sampleConditions
        );
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

    @Test(timeout = 5000)
    @Category({UnitTest.class})
    public void itPostNewRuleWithNoEtagResultHasAllFields() throws Exception {
        CompletionStage<RuleServiceModel> ruleResult =
            Callback.Completable.completedFuture(null);

        IRules rules = mock(IRules.class);

        RulesController controller = new RulesController(rules);
        when(rules.postAsync(any())).thenReturn(ruleResult);

        mockHttpContext(new RuleApiModel(this.sampleNewRuleServiceModel));

        // Act
        controller.postAsync().thenApply(response -> {
            // Assert - that body is not null
            assertThat(response.body().isKnownEmpty(), is(false));

            // Assert - that body contains all fields
            JsonNode responseBody = Json.parse(response.body().toString());
            assertTrue(responseBody.hasNonNull("ETag"));
            assertTrue(responseBody.hasNonNull("Id"));
            assertTrue(responseBody.hasNonNull("Name"));
            assertTrue(responseBody.hasNonNull("DateCreated"));
            assertTrue(responseBody.hasNonNull("DateModified"));
            assertTrue(responseBody.hasNonNull("Enabled"));
            assertTrue(responseBody.hasNonNull("Description"));
            assertTrue(responseBody.hasNonNull("GroupId"));
            assertTrue(responseBody.hasNonNull("Severity"));
            assertTrue(responseBody.hasNonNull("Conditions"));

            return null;
        });
    }

    @Test(timeout = 5000)
    @Category({UnitTest.class})
    public void itPutNewRuleWithNoEtagResultHasAllFields() throws Exception {
        CompletionStage<RuleServiceModel> ruleResult =
            Callback.Completable.completedFuture(null);

        IRules rules = mock(IRules.class);

        RulesController controller = new RulesController(rules);
        when(rules.putAsync(any())).thenReturn(ruleResult);

        mockHttpContext(new RuleApiModel(this.sampleNewRuleServiceModel));

        // Act
        controller.putAsync(this.sampleNewRuleServiceModel.getId()).thenApply(response -> {
            // Assert - that body is not null
            assertThat(response.body().isKnownEmpty(), is(false));

            // Assert - that body contains all fields
            JsonNode responseBody = Json.parse(response.body().toString());
            assertTrue(responseBody.hasNonNull("ETag"));
            assertTrue(responseBody.hasNonNull("Id"));
            assertTrue(responseBody.hasNonNull("Name"));
            assertTrue(responseBody.hasNonNull("DateCreated"));
            assertTrue(responseBody.hasNonNull("DateModified"));
            assertTrue(responseBody.hasNonNull("Enabled"));
            assertTrue(responseBody.hasNonNull("Description"));
            assertTrue(responseBody.hasNonNull("GroupId"));
            assertTrue(responseBody.hasNonNull("Severity"));
            assertTrue(responseBody.hasNonNull("Conditions"));

            return null;
        });
    }

    private void mockHttpContext(Object requestBody) {
        Http.Request mockRequest = mock(Http.Request.class);
        when(mockRequest.body()).thenReturn(new Http.RequestBody(toJson(requestBody)));

        Http.Context mockContext = mock(Http.Context.class);
        when(mockContext.request()).thenReturn(mockRequest);

        Http.Context.current.set(mockContext);
    }
}
