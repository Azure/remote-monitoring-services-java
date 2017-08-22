// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.ResourceOutOfDateException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.ConditionServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.RuleServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServicesConfig;
import play.Logger;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;

public final class Rules implements IRules {

    private static final int CONFLICT = 409;
    private static final int NOT_FOUND = 404;
    private static final int OK = 200;
    private static final Logger.ALogger log = Logger.of(Rules.class);

    private final String storageUrl;
    private final WSClient wsClient;

    @Inject
    public Rules(
        final IServicesConfig servicesConfig,
        final WSClient wsClient) {

        this.storageUrl = servicesConfig.getKeyValueStorageUrl() + "/collections/rules/values";
        this.wsClient = wsClient;
    }

    public CompletionStage<RuleServiceModel> getAsync(String id) {

        return this.prepareRequest(id)
            .get()
            .handle((result, error) -> {

                if (error != null) {
                    log.error("Key value storage request error: {}",
                        error.getMessage());
                    throw new CompletionException(
                        new ExternalDependencyException(error.getMessage()));
                }

                if (result.getStatus() == NOT_FOUND) {
                    return null;
                }

                try {
                    return getRuleServiceModelFromJson(Json.parse(result.getBody()));
                } catch (Exception e) {
                    log.error("Could not parse result from Key Value Storage: {}",
                        e.getMessage());
                    throw new CompletionException(
                        new ExternalDependencyException(
                            "Could not parse result from Key Value Storage"));
                }
            });
    }

    public CompletionStage<List<RuleServiceModel>> getListAsync(
        String order,
        int skip,
        int limit,
        String groupId) {

        if (skip < 0 || limit <= 0) {
            log.error("Key value storage parameter bounds error");
            throw new CompletionException(
                new InvalidInputException("Parameter bounds error"));
        }

        return this.prepareRequest(null)
            .get()
            .handle((result, error) -> {

                if (error != null) {
                    log.error("Key value storage request error: {}",
                        error.getMessage());
                    throw new CompletionException(
                        new ExternalDependencyException(error.getMessage()));
                }

                try {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode jsonResult = mapper.readTree(result.getBody());

                    ArrayList<JsonNode> jsonList =
                        getResultListFromJson(jsonResult);

                    ArrayList<RuleServiceModel> ruleList = new ArrayList<>();

                    for (JsonNode resultItem : jsonList) {
                        RuleServiceModel rule =
                            getRuleServiceModelFromJson(resultItem);

                        if (groupId == null ||
                            groupId.equalsIgnoreCase(rule.getGroupId())) {
                            ruleList.add(rule);
                        }
                    }

                    if (ruleList.isEmpty()) {
                        return ruleList;
                    }

                    if (order.equalsIgnoreCase("asc")) {
                        Collections.sort(ruleList);
                    } else {
                        Collections.sort(ruleList, Collections.reverseOrder());
                    }

                    if (skip >= ruleList.size()) {
                        log.debug("Skip value greater than size of listAsync");
                        return new ArrayList<>();
                    } else if ((limit + skip) > ruleList.size()) {
                        return ruleList.subList(skip, ruleList.size());
                    }

                    return ruleList.subList(skip, limit + skip);

                } catch (Exception e) {
                    log.error("Could not parse result from Key Value Storage: {}",
                        e.getMessage());
                    throw new CompletionException(
                        new ExternalDependencyException(
                            "Could not parse result from Key Value Storage"));
                }
            });
    }

    public CompletionStage<RuleServiceModel> postAsync(
        RuleServiceModel ruleServiceModel) {

        JsonNode jsonRule = Json.toJson(ruleServiceModel);

        ObjectNode jsonData = new ObjectMapper().createObjectNode();
        jsonData.put("Data", jsonRule.toString());

        return this.prepareRequest(null)
            .post(jsonData.toString())
            .handle((result, error) -> {

                if (result.getStatus() != OK) {
                    log.error("Key value storage error code {}",
                        result.getStatusText());
                    throw new CompletionException(
                        new ExternalDependencyException(result.getStatusText()));
                }

                if (error != null) {
                    log.error("Key value storage request error: {}",
                        error.getMessage());
                    throw new CompletionException(
                        new ExternalDependencyException(error.getMessage()));
                }

                try {
                    return getRuleServiceModelFromJson(
                        Json.parse(result.getBody()));
                } catch (Exception e) {
                    log.error("Could not parse result from Key Value Storage: {}",
                        e.getMessage());
                    throw new CompletionException(
                        new ExternalDependencyException(
                            "Could not parse result from Key Value Storage"));
                }
            });
    }

    public CompletionStage<RuleServiceModel> putAsync(RuleServiceModel ruleServiceModel) {

        JsonNode jsonRule = Json.toJson(ruleServiceModel);

        ObjectNode jsonData = new ObjectMapper().createObjectNode();
        jsonData.put("Data", jsonRule.toString());
        jsonData.put("ETag", ruleServiceModel.getETag());

        return this.prepareRequest(ruleServiceModel.getId())
            .put(jsonData.toString())
            .handle((result, error) -> {

                if (error != null) {
                    log.error("Key value storage request error: {}",
                        error.getMessage());
                    throw new CompletionException(
                        new ExternalDependencyException(error.getMessage()));
                }

                if (result.getStatus() == CONFLICT) {
                    log.error("Key value storage ETag mismatch");
                    throw new CompletionException(
                        new ResourceOutOfDateException());
                } else if (result.getStatus() != OK) {
                    log.error("Key value storage error code {}",
                        result.getStatusText());
                    throw new CompletionException(
                        new ExternalDependencyException(result.getStatusText()));
                }

                try {
                    return getRuleServiceModelFromJson(
                        Json.parse(result.getBody()));
                } catch (Exception e) {
                    log.error("Could not parse result from Key Value Storage: {}",
                        e.getMessage());
                    throw new CompletionException(
                        new ExternalDependencyException(
                            "Could not parse result from Key Value Storage"));
                }
            });
    }

    public CompletionStage deleteAsync(String id) {

        return this.prepareRequest(id)
            .delete()
            .handle((result, error) -> {
                if (error != null) {
                    log.error("Key value storage request error: {}",
                        error.getMessage());
                    throw new CompletionException(
                        new ExternalDependencyException(error.getMessage()));
                }

                return true;
            });
    }

    private WSRequest prepareRequest(String id) {

        String url = this.storageUrl;
        if (id != null) {
            url = url + "/" + id;
        }

        WSRequest wsRequest = this.wsClient
            .url(url)
            .addHeader("Content-Type", "application/json");

        return wsRequest;
    }

    private ArrayList<JsonNode> getResultListFromJson(JsonNode response) {

        ArrayList<JsonNode> resultList = new ArrayList<>();

        for (JsonNode item : response.withArray("items")) {
            try {
                resultList.add(item);
            } catch (Exception e) {
                log.error("Could not parse data from Key Value Storage");
                throw new CompletionException(
                    new ExternalDependencyException(
                        "Could not parse data from Key Value Storage"));
            }
        }

        return resultList;
    }

    private RuleServiceModel getRuleServiceModelFromJson(JsonNode response) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonResultRule = null;

        try {
            jsonResultRule = mapper.readTree(response.findValue("Data").asText());
        } catch (Exception e) {
            log.error("Could not parse data from Key Value Storage. " +
                "Json result: {}", jsonResultRule.asText());
            throw new CompletionException(
                new ExternalDependencyException(
                    "Could not parse data from Key Value Storage"));
        }

        if (jsonResultRule != null) {
            ArrayList<ConditionServiceModel> conditions = new ArrayList<>();
            for (JsonNode condition : jsonResultRule.withArray("conditions")) {
                conditions.add(
                    new ConditionServiceModel(
                        condition.findValue("field").asText(),
                        condition.findValue("operator").asText(),
                        condition.findValue("value").asText())
                );
            }

            return new RuleServiceModel(
                response.findValue("ETag").asText(),
                response.findValue("Key").asText(),
                jsonResultRule.findValue("name").asText(),
                jsonResultRule.findValue("dateCreated").asText(),
                jsonResultRule.findValue("dateModified").asText(),
                jsonResultRule.findValue("enabled").asBoolean(),
                jsonResultRule.findValue("description").asText(),
                jsonResultRule.findValue("groupId").asText(),
                jsonResultRule.findValue("severity").asText(),
                conditions
            );
        }
        return null;
    }
}
