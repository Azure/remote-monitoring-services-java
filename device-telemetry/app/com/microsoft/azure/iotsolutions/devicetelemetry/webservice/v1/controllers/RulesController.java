// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.IRules;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidConfigurationException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.ResourceNotFoundException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.ResourceOutOfDateException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.ConditionServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.RuleServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.ConditionApiModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.RuleApiModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.RuleListApiModel;
import org.joda.time.DateTime;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static play.libs.Json.toJson;

public class RulesController extends Controller {

    private static final int CONFLICT = 409;

    private static final Logger.ALogger log = Logger.of(RulesController.class);

    private final IRules rules;

    @Inject
    public RulesController(final IRules rules) {
        this.rules = rules;
    }

    /**
     * Return a listof rules. The list of rules can be paginated, and
     * filtered by device and status. The list is sorted
     * chronologically, by default starting from the oldest alarm, and
     * optionally from the most recent.
     *
     * @return List of rules
     */
    public CompletionStage<Result> listAsync(
        String order,
        Integer skip,
        Integer limit,
        String groupId) {

        if (order == null) order = "asc";
        if (skip == null) skip = 0;
        if (limit == null) limit = 1000;

        return this.rules.getListAsync(
            order,
            skip,
            limit,
            groupId)
            .thenApply(
                ruleServiceModelList ->
                    ok(toJson(new RuleListApiModel(ruleServiceModelList))));
    }

    /**
     * @return One rule
     */
    public CompletionStage<Result> getAsync(String id) {

        return
            rules.getAsync(id)
                .thenApply(
                    rule -> {
                        if (rule != null) {
                            return ok(toJson(new RuleApiModel(rule)));
                        } else {
                            return notFound();
                        }
                    });
    }

    /**
     * Returns a newly created rule if id is NULL, or modifies existing
     * rule with specified id.
     *
     * @return newly created rule
     */
    public CompletionStage<Result> postAsync(String template) {

        if (template != null) { // create rules with template

            return CompletableFuture.supplyAsync(() -> {
                try {
                    rules.createFromTemplate(template);
                    return ok();
                } catch (InvalidConfigurationException e) {
                    return internalServerError();
                } catch (ResourceNotFoundException e) {
                    return notFound(e.getMessage());
                }
            });
        } else { // create rule with request body

            JsonNode jsonBody = request().body().asJson();

            if (jsonBody == null) {
                log.warn("The request is empty");
                return CompletableFuture.completedFuture(
                    badRequest("The request is empty"));
            }

            ArrayList<ConditionServiceModel> conditions = new ArrayList<>();
            for (JsonNode condition : jsonBody.withArray("Conditions")) {
                conditions.add(Json.fromJson(
                    condition,
                    ConditionApiModel.class).toServiceModel());
            }

            RuleServiceModel ruleServiceModel = null;

            try {
                ruleServiceModel = new RuleServiceModel(
                    jsonBody.findValue("Name").asText(),
                    jsonBody.findValue("Enabled").asBoolean(),
                    jsonBody.findValue("Description").asText(),
                    jsonBody.findValue("GroupId").asText(),
                    jsonBody.findValue("Severity").asText(),
                    conditions);
            } catch (Exception e) {
                return CompletableFuture.completedFuture(
                    badRequest("Invalid input"));
            }

            return rules.postAsync(ruleServiceModel)
                .thenApply(newRule -> ok(toJson(new RuleApiModel(newRule))));
        }
    }

    /**
     * Modify existing rule with specified id.
     *
     * @return updated rule
     */
    public CompletionStage<Result> putAsync(String id) {
        JsonNode jsonBody = request().body().asJson();

        if (jsonBody == null) {
            log.warn("The request is empty");
            return CompletableFuture.completedFuture(
                badRequest("The request is empty"));
        }

        ArrayList<ConditionServiceModel> conditions = new ArrayList<>();

        for (JsonNode condition : jsonBody.withArray("Conditions")) {
            conditions.add(Json.fromJson(
                condition,
                ConditionApiModel.class).toServiceModel());
        }

        RuleServiceModel ruleServiceModel = null;

        try {
            ruleServiceModel = new RuleServiceModel(
                jsonBody.findValue("ETag").asText(),
                id,
                jsonBody.findValue("Name").asText(),
                jsonBody.findValue("DateCreated").asText(),
                DateTime.now().toString(),
                jsonBody.findValue("Enabled").asBoolean(),
                jsonBody.findValue("Description").asText(),
                jsonBody.findValue("GroupId").asText(),
                jsonBody.findValue("Severity").asText(),
                conditions);

        } catch (Exception e) {
            return CompletableFuture.completedFuture(
                badRequest("Invalid input"));
        }

        RuleServiceModel finalRuleServiceModel = ruleServiceModel;
        return rules.putAsync(finalRuleServiceModel)
            .thenApply(newRule -> ok(toJson(new RuleApiModel(newRule))))
            .exceptionally(e -> {
                if (e.getCause() instanceof ResourceOutOfDateException) {
                    return status(CONFLICT, Json.toJson(finalRuleServiceModel));
                } else {
                    return internalServerError(e.getMessage());
                }
            });
    }

    /**
     * Delete rule with specified id
     *
     * @return OK
     */
    public CompletionStage<Result> deleteAsync(String id) {

        return rules.deleteAsync(id).thenApply(success -> ok());
    }
}
