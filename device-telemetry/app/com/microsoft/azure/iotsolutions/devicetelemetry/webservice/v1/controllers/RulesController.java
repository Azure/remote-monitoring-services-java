// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.IRules;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.ResourceOutOfDateException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.ConditionServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.RuleServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.exceptions.BadRequestException;
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

        log.info("Trying to list rules with parameters: " + "order: " + order +
            ", skip: " + skip + ", limit: " + limit + ", groupId: " + groupId);

        return this.rules.getListAsync(
            order,
            skip,
            limit,
            groupId)
            .thenApply(
                ruleServiceModelList -> {
                    log.info("Successfully retrieved rules list.");
                    return ok(toJson(new RuleListApiModel(ruleServiceModelList)));
                });
    }

    /**
     * @return One rule
     */
    public CompletionStage<Result> getAsync(String id) {

        log.info("Trying to get rule id " + id);

        return
            rules.getAsync(id)
                .thenApply(
                    rule -> {
                        if (rule != null) {
                            log.info("Successfully retrieved rule id " + id);
                            return ok(toJson(new RuleApiModel(rule)));
                        } else {
                            log.info("Rule id " + id + " not found.");
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
            log.info("Trying to parse rule from POST request. Input: " + jsonBody);

            ruleServiceModel = requestBodyToRuleServiceModel(jsonBody, null);

            log.info("Successfully parsed rule id " + ruleServiceModel.getId() +
                " from POST request.");
        } catch (Exception e) {
            String msg = "Could not parse rule body from POST request. Error Msg: " +
                e.getMessage();
            log.error(msg, e, jsonBody);
            return CompletableFuture.completedFuture(badRequest(msg));
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
            log.info("Trying to parse rule from PUT request. Input: " + jsonBody);

            ruleServiceModel = requestBodyToRuleServiceModel(jsonBody, id);

            log.info("Successfully parsed rule id " + id + " from PUT request.");
        } catch (Exception e) {
            String msg = "Could not complete PUT request. Error Msg: "
                + e.getMessage();
            log.error(msg, e, jsonBody);
            return CompletableFuture.completedFuture(badRequest(msg));
        }

        return rules.putAsync(ruleServiceModel)
            .thenApply(newRule -> ok(toJson(new RuleApiModel(newRule))))
            .exceptionally(e -> {
                if (e.getCause() instanceof ResourceOutOfDateException) {
                    log.info("Etag conflict, could not update rule. Error Msg: "
                        + e.getMessage(), e);
                    return status(
                        CONFLICT,
                        "Request ETag and storage ETag mismatch");
                } else {
                    String msg = "Could not complete PUT request. Error msg: "
                        + e.getMessage();
                    log.error(msg);
                    return internalServerError(msg);
                }
            });
    }

    /**
     * Delete rule with specified id
     *
     * @return OK
     */
    public CompletionStage<Result> deleteAsync(String id) {
        log.info("Trying to delete rule id " + id + ".");
        return rules.deleteAsync(id).thenApply(success -> ok());
    }

    /**
     * Converts Json request body to Rule service model. Throws exception if
     * input is invalid.
     *
     * @return RuleServiceModel
     */
    private RuleServiceModel requestBodyToRuleServiceModel(JsonNode body, String id)
        throws BadRequestException {

        RuleServiceModel result;

        if (body == null) {
            String msg = "The request is empty.";
            log.warn(msg);
            throw new BadRequestException(msg);
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
            String msg = "Invalid input, " +
                "request does not contain the `Conditions` field. Error msg: " +
                e.getMessage();
            log.error(msg, e);
            throw new BadRequestException(msg, e);

        }

        // update existing Rule if ETag is present
        if (body.has("ETag") ||
            body.has("etag") ||
            body.has("Etag")) {

            // Rule ID must be specified in the request e.g. rules/{put-rule-id}
            if (id == null) {
                String msg = "Bad request -- null rule id in PUT request " +
                    "with etag in body. Must specify rule id in request URL.";
                log.error(msg);
                throw new BadRequestException(msg);
            }

            try {
                log.info("Try to create new RuleServiceModel with id " + id +
                    " from json: " + body);

                result = new RuleServiceModel(
                    JsonHelper.getNode(body, "ETag").asText(),
                    id,
                    JsonHelper.getNode(body, "Name").asText(),
                    JsonHelper.getNode(body, "Enabled").asBoolean(),
                    JsonHelper.getNode(body, "Description").asText(),
                    JsonHelper.getNode(body, "GroupId").asText(),
                    JsonHelper.getNode(body, "Severity").asText(),
                    conditions);
            } catch (Exception e) {
                String msg = "Invalid input for rule with ETag. Error msg: "
                    + e.getMessage();
                log.error(msg, e);
                throw new BadRequestException(msg, e);
            }

        } else if (id != null) { // create new rule with specified id
            try {
                log.info("Try to create new rule with id " + id +
                    " from json: " + body);

                result = new RuleServiceModel(
                    id,
                    JsonHelper.getNode(body, "Name").asText(),
                    JsonHelper.getNode(body, "Enabled").asBoolean(),
                    JsonHelper.getNode(body, "Description").asText(),
                    JsonHelper.getNode(body, "GroupId").asText(),
                    JsonHelper.getNode(body, "Severity").asText(),
                    conditions);
            } catch (Exception e) {
                String msg = "Invalid input for new rule with id. Error msg: " +
                    e.getMessage();
                log.error(msg, e);
                throw new BadRequestException(msg, e);
            }
        } else { // otherwise create new rule
            log.info("Try to create new rule with no id provided " +
                "from json: " + body);

            try {
                result = new RuleServiceModel(
                    JsonHelper.getNode(body, "Name").asText(),
                    JsonHelper.getNode(body, "Enabled").asBoolean(),
                    JsonHelper.getNode(body, "Description").asText(),
                    JsonHelper.getNode(body, "GroupId").asText(),
                    JsonHelper.getNode(body, "Severity").asText(),
                    conditions);
            } catch (Exception e) {
                String msg = "Invalid input for new rule. Error msg: " +
                    e.getMessage();
                log.error(msg, e);
                throw new BadRequestException(msg, e);
            }
        }

        return result;
    }
}
