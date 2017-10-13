// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.IRules;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.ResourceOutOfDateException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.ConditionServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.RuleServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.ConditionApiModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.RuleApiModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.RuleListApiModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.serialization.JsonHelper;
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
     * Return a list of rules. The list of rules can be paginated, and
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
     * Returns a newly created rule
     *
     * @return newly created rule
     */
    public CompletionStage<Result> postAsync() {

        JsonNode jsonBody = request().body().asJson();

        RuleServiceModel ruleServiceModel;

        // convert body to RuleServiceModel
        try {
            ruleServiceModel = requestBodyToRuleServiceModel(jsonBody, null);
        }
        catch (Exception e) {
            return CompletableFuture.completedFuture(
                badRequest(e.getMessage()));
        }

        return rules.postAsync(ruleServiceModel)
            .thenApply(newRule -> ok(toJson(new RuleApiModel(newRule))));

    }

    /**
     * Modify existing rule with specified id,
     * or create new rule if id doesn't exist
     *
     * @return updated rule
     */
    public CompletionStage<Result> putAsync(String id) {

        JsonNode jsonBody = request().body().asJson();

        RuleServiceModel ruleServiceModel;

        // convert body to RuleServiceModel
        try {
            ruleServiceModel = requestBodyToRuleServiceModel(jsonBody, id);
        }
        catch (Exception e) {
            return CompletableFuture.completedFuture(
                badRequest(e.getMessage()));
        }

        return rules.putAsync(ruleServiceModel)
            .thenApply(newRule -> ok(toJson(new RuleApiModel(newRule))))
            .exceptionally(e -> {
                if (e.getCause() instanceof ResourceOutOfDateException) {
                    return status(
                        CONFLICT,
                        "Request ETag and storage ETag mismatch");
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

    /**
     * Converts Json request body to Rule service model. Throws exception if
     * input is invalid.
     *
     * @return RuleServiceModel
     */
    private RuleServiceModel requestBodyToRuleServiceModel(JsonNode body, String id) throws Exception {

        RuleServiceModel result;

        if (body == null) {
            log.warn("The request is empty");
            throw new Exception("The request is empty");
        }

        // parse conditions, ignore key case
        ArrayList<ConditionServiceModel> conditions = new ArrayList<>();
        String conditionsKey = body.has("Conditions") ? "Conditions" : "conditions";
        try {
            for (JsonNode condition : body.withArray(conditionsKey)) {
                conditions.add(Json.fromJson(
                    condition,
                    ConditionApiModel.class).toServiceModel());
            }
        } catch (Exception e) {
            throw new Exception("Invalid input, " +
                "request does not contain the `Conditions` field");
        }

        // update existing Rule if ETag is present
        if (body.has("ETag") || body.has("etag")) {
            try {
                result = new RuleServiceModel(
                    JsonHelper.getNode(body, "ETag").asText(),
                    id,
                    JsonHelper.getNode(body, "Name").asText(),
                    JsonHelper.getNode(body, "DateCreated").asText(),
                    JsonHelper.getNode(body, "Enabled").asBoolean(),
                    JsonHelper.getNode(body, "Description").asText(),
                    JsonHelper.getNode(body, "GroupId").asText(),
                    JsonHelper.getNode(body, "Severity").asText(),
                    conditions);
            } catch (Exception e) {
                throw new Exception("Invalid input");
            }
        } else if (id != null) { // create new rule with specified id
            try {
                result = new RuleServiceModel(
                    id,
                    JsonHelper.getNode(body, "Name").asText(),
                    JsonHelper.getNode(body, "Enabled").asBoolean(),
                    JsonHelper.getNode(body, "Description").asText(),
                    JsonHelper.getNode(body, "GroupId").asText(),
                    JsonHelper.getNode(body, "Severity").asText(),
                    conditions);
            } catch (Exception e) {
                throw new Exception("Invalid input");
            }
        } else { // otherwise create new rule
            try {
                result = new RuleServiceModel(
                    JsonHelper.getNode(body, "Name").asText(),
                    JsonHelper.getNode(body, "Enabled").asBoolean(),
                    JsonHelper.getNode(body, "Description").asText(),
                    JsonHelper.getNode(body, "GroupId").asText(),
                    JsonHelper.getNode(body, "Severity").asText(),
                    conditions);
            } catch (Exception e) {
                throw new Exception("Invalid input");
            }
        }

        return result;
    }
}
