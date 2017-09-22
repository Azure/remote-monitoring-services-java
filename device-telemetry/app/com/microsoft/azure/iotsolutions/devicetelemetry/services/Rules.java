// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.*;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.ConditionServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.RuleServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.ConditionApiModel;
import play.Logger;
import play.api.Play;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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

        ObjectNode jsonData = new ObjectMapper().createObjectNode();
        jsonData.put("Data", ruleServiceModel.toJson().toString());

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
                        new ExternalDependencyException(
                            "Could not connect to key value storage " +
                            error.getMessage()));
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

    public void createFromTemplate(String template)
        throws InvalidConfigurationException, ResourceNotFoundException {

        InputStream stream = Play.current().classloader()
            .getResourceAsStream(template + ".json");

        if (stream != null) {
            try {
                JsonNode jsonTemplate = Json.parse(stream);

                for (JsonNode item : jsonTemplate.withArray("Rules")) {
                    RuleServiceModel rule = getRuleServiceModelFromTemplate(item);
                    postAsync(rule);
                }

            } catch (Exception e) {
                log.error("Could not read rules from given template" +
                    " file {}.json", template);
                throw new InvalidConfigurationException(
                    "Could not read rules from given template file");
            }
        } else {
            throw new ResourceNotFoundException(
                "Could not find template file " + template + ".json");
        }
    }

    public CompletionStage<RuleServiceModel> putAsync(RuleServiceModel ruleServiceModel) {

        ObjectNode jsonData = new ObjectMapper().createObjectNode();
        jsonData.put("Data", ruleServiceModel.toJson().toString());
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
                        new ResourceOutOfDateException(
                            "Key value storage ETag mismatch"));
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
            jsonResultRule = mapper.readTree(
                getJsonNodeIgnoreCase(response, "Data").asText());
        } catch (Exception e) {
            log.error("Could not parse data from Key Value Storage. " +
                "Json result: {}", jsonResultRule.asText());
            throw new CompletionException(
                new ExternalDependencyException(
                    "Could not parse data from Key Value Storage"));
        }

        if (jsonResultRule != null) {
            ArrayList<ConditionServiceModel> conditions = new ArrayList<>();
            for (JsonNode condition : getJsonNodeIgnoreCase(jsonResultRule,
                "conditions")) {
                conditions.add(
                    new ConditionServiceModel(
                        getJsonNodeIgnoreCase(condition, "field").asText(),
                        getJsonNodeIgnoreCase(condition, "operator").asText(),
                        getJsonNodeIgnoreCase(condition, "value").asText())
                );
            }

            return new RuleServiceModel(
                getJsonNodeIgnoreCase(response, "ETag").asText(),
                getJsonNodeIgnoreCase(response, "Key").asText(),
                getJsonNodeIgnoreCase(jsonResultRule, "name").asText(),
                getJsonNodeIgnoreCase(jsonResultRule, "dateCreated").asText(),
                getJsonNodeIgnoreCase(jsonResultRule, "dateModified").asText(),
                getJsonNodeIgnoreCase(jsonResultRule, "enabled").asBoolean(),
                getJsonNodeIgnoreCase(jsonResultRule, "description").asText(),
                getJsonNodeIgnoreCase(jsonResultRule, "groupId").asText(),
                getJsonNodeIgnoreCase(jsonResultRule, "severity").asText(),
                conditions
            );
        }
        return null;
    }

    private RuleServiceModel getRuleServiceModelFromTemplate(JsonNode rule) {
        ArrayList<ConditionServiceModel> conditions = new ArrayList<>();

        Boolean matchesSchema =
            rule.has("Name") &&
            rule.has("Enabled") &&
            rule.has("Description") &&
            rule.has("GroupId") &&
            rule.has("Severity") &&
            rule.has("Conditions");

        if (!matchesSchema) {
            log.error("Rule template does not match rule schema. "
                + rule.asText());
            throw new CompletionException(
                new InvalidConfigurationException(
                    "Rule template does not match rule schema. "
                        + rule.asText()));
        }

        for (JsonNode condition : getJsonNodeIgnoreCase(rule,
            "Conditions")) {
            conditions.add(Json.fromJson(
                condition,
                ConditionApiModel.class).toServiceModel());
        }

        return new RuleServiceModel(
            getJsonNodeIgnoreCase(rule, "Name").asText(),
            getJsonNodeIgnoreCase(rule, "Enabled").asBoolean(),
            getJsonNodeIgnoreCase(rule, "Description").asText(),
            getJsonNodeIgnoreCase(rule, "GroupId").asText(),
            getJsonNodeIgnoreCase(rule, "Severity").asText(),
            conditions);
    }

    // returns the object associated with the given key, ignoring case
    // if object cannot be found, returns null
    private JsonNode getJsonNodeIgnoreCase(JsonNode node, String key) {

        Iterator<String> fieldNames = node.fieldNames();

        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            if (fieldName.equalsIgnoreCase(key)) {
                return node.get(fieldName);
            }
        }

        return null;
    }
}
