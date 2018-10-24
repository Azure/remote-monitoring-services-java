// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.IRules;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.auth.Authorize;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.ResourceOutOfDateException;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.RuleApiModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.RuleListApiModel;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.concurrent.CompletionStage;

import static play.libs.Json.fromJson;
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
        String groupId,
        Boolean includeDeleted) {

        if (order == null) order = "asc";
        if (skip == null) skip = 0;
        if (limit == null) limit = 1000;
        if (includeDeleted == null) includeDeleted = false;

        log.info("Trying to list rules with parameters: " + "order: " + order +
            ", skip: " + skip + ", limit: " + limit + ", groupId: " + groupId);
        boolean showDeletedStatus = includeDeleted;

        return this.rules.getListAsync(
            order,
            skip,
            limit,
            groupId,
            includeDeleted)
            .thenApply(
                ruleServiceModelList -> {
                    log.info("Successfully retrieved rules list.");
                    return ok(toJson(new RuleListApiModel(ruleServiceModelList, showDeletedStatus)));
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
                            return ok(toJson(new RuleApiModel(rule, rule.getDeleted())));
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
    @Authorize("CreateRules")
    public CompletionStage<Result> postAsync() {
        log.info("Trying to create a new rule.");
        JsonNode requestBody = request().body().asJson();
        RuleApiModel ruleApiModel = fromJson(request().body().asJson(), RuleApiModel.class);
        if (ruleApiModel == null) {
            badRequest(request().body().asText());
        }
        return rules.postAsync(ruleApiModel.toServiceModel())
            .thenApply(newRule -> ok(toJson(new RuleApiModel(newRule, false))));
    }

    /**
     * Modify existing rule with specified id,
     * or create new rule if id doesn't exist
     *
     * @return updated rule
     */
    @Authorize("UpdateRules")
    public CompletionStage<Result> putAsync(String id) {
        log.info("Trying to update rule id " + id + ".");
        RuleApiModel ruleApiModel = fromJson(request().body().asJson(), RuleApiModel.class);
        if (ruleApiModel == null) {
            badRequest(request().body().asText());
        }
        return rules.upsertIfNotDeletedAsync(ruleApiModel.toServiceModel(id))
            .thenApply(newRule -> ok(toJson(new RuleApiModel(newRule, false))))
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
    @Authorize("DeleteRules")
    public CompletionStage<Result> deleteAsync(String id) {
        log.info("Trying to delete rule id " + id + ".");
        return rules.deleteAsync(id).thenApply(success -> ok());
    }
}
