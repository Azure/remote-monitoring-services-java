// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.ResourceNotFoundException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.ResourceOutOfDateException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.AlarmCountByRuleServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.AlarmServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.RuleServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.serialization.JsonHelper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import play.Logger;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;

public final class Rules implements IRules {

    private final static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZZ";
    private static final int CONFLICT = 409;
    private static final int NOT_FOUND = 404;
    private static final int OK = 200;
    private static final Logger.ALogger log = Logger.of(Rules.class);

    private final String storageUrl;
    private final WSClient wsClient;

    private final IAlarms alarmsService;

    @Inject
    public Rules(
        final IServicesConfig servicesConfig,
        final WSClient wsClient,
        final IAlarms alarmsService) {

        this.storageUrl = servicesConfig.getKeyValueStorageUrl() + "/collections/rules/values";
        this.wsClient = wsClient;
        this.alarmsService = alarmsService;
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
                    log.info("Rule id " + id + " not found.");
                    return null;
                }

                try {
                    return getServiceModelFromJson(Json.parse(result.getBody()));
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
                            getServiceModelFromJson(resultItem);

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

    @Override
    public CompletionStage<List<AlarmCountByRuleServiceModel>> getAlarmCountForList(
        DateTime from,
        DateTime to,
        String order,
        int skip,
        int limit,
        String[] devices
    ) throws ExternalDependencyException {

        ArrayList<AlarmCountByRuleServiceModel> alarmByRuleList = new ArrayList<>();

        // get list of rules
        return this.getListAsync(order, skip, limit, null)
            .thenApply(rulesList -> {

                // get open alarm count and most recent alarm for each rule
                for (RuleServiceModel rule : rulesList) {

                    int alarmCount;
                    try {
                        alarmCount = this.alarmsService.getCountByRuleId(
                            rule.getId(),
                            from,
                            to,
                            devices);
                    } catch (java.lang.Exception e) {
                        log.error("Could not retrieve alarm count for " +
                            "rule id {}", rule.getId(), e);
                        throw new CompletionException(
                            new ExternalDependencyException(
                                "Could not retrieve alarm count for " +
                                    "rule id " + rule.getId(), e));
                    }

                    // skip to next rule if no alarms found
                    if (alarmCount == 0) {
                        continue;
                    }

                    // get most recent alarm for rule
                    AlarmServiceModel recentAlarm = this.getMostRecentAlarmForRule(
                        rule.getId(),
                        from,
                        to,
                        devices);

                    // should always find alarm at this point
                    if (recentAlarm == null) {
                        log.error("Alarm count mismatch -- could not " +
                            "find alarm for rule id {} when alarm count for " +
                            "rule is {}.", rule.getId(), alarmCount);
                        throw new CompletionException(
                            new ExternalDependencyException(
                                "Alarm count mismatch -- could not " +
                                    "find alarm for rule id " + rule.getId()));
                    }

                    // Add alarm by rule to list
                    alarmByRuleList.add(
                        new AlarmCountByRuleServiceModel(
                            alarmCount,
                            recentAlarm.getStatus(),
                            recentAlarm.getDateCreated(),
                            rule));
                }

                return alarmByRuleList;
            });
    }

    public CompletionStage<RuleServiceModel> postAsync(
        RuleServiceModel ruleServiceModel) {

        // Ensure dates are correct
        ruleServiceModel.setDateCreated(DateTime.now(DateTimeZone.UTC).toString(DATE_FORMAT));
        ruleServiceModel.setDateModified(ruleServiceModel.getDateCreated());

        ObjectNode jsonData = new ObjectMapper().createObjectNode();
        jsonData.put("Data", ruleServiceModel.toJsonString());

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
                    return getServiceModelFromJson(
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
        // Ensure dates are correct
        // Get the existing rule so we keep the created date correct; update the modified date to now
        try {
            CompletableFuture<RuleServiceModel> savedRuleFuture = getAsync(ruleServiceModel.getId()).toCompletableFuture();
            RuleServiceModel savedRule = savedRuleFuture.get();
            if (savedRule == null) {
                throw new CompletionException(
                    new ResourceNotFoundException(ruleServiceModel.getId()));
            }

            ruleServiceModel.setDateCreated(savedRule.getDateCreated());
            ruleServiceModel.setDateModified(DateTime.now(DateTimeZone.UTC).toString(DATE_FORMAT));
        } catch (Exception e) {
            log.error("Could not get existing rule from Key Value Storage: {}", e.getMessage());
            throw new CompletionException(
                new ResourceNotFoundException(ruleServiceModel.getId()));
        }

        ObjectNode jsonData = new ObjectMapper().createObjectNode();
        jsonData.put("Data", ruleServiceModel.toJsonString());
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
                    RuleServiceModel rule =
                        getServiceModelFromJson(Json.parse(result.getBody()));

                    log.info("Successfully retrieved rule id " + rule.getId());

                    return rule;
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

                log.info("Successfully deleted rule id " + id);
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

    private AlarmServiceModel getMostRecentAlarmForRule(
        String ruleId,
        DateTime from,
        DateTime to,
        String[] devices) {

        AlarmServiceModel result = null;

        try {
            ArrayList<AlarmServiceModel> resultList
                = this.alarmsService.getListByRuleId(
                ruleId,
                from,
                to,
                "desc",
                0,
                1,
                devices);

            if (resultList.size() > 0) {
                result = resultList.get(0);
            }
        } catch (java.lang.Exception e) {
            String errorMsg = "Could not retrieve most recent alarm " +
                "for rule id " + ruleId;
            log.error(errorMsg, e);
            throw new CompletionException(
                new ExternalDependencyException(errorMsg, e));
        }

        return result;
    }

    private ArrayList<JsonNode> getResultListFromJson(JsonNode response) {

        ArrayList<JsonNode> resultList = new ArrayList<>();

        // ignore case when parsing items array
        String itemsKey = response.has("Items") ? "Items" : "items";

        for (JsonNode item : response.withArray(itemsKey)) {
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

    private RuleServiceModel getServiceModelFromJson(JsonNode response) {
        String jsonResultRule = null;

        try {
            jsonResultRule = JsonHelper.getNode(response, "Data").asText();
            RuleServiceModel rule = new ObjectMapper().readValue(jsonResultRule, RuleServiceModel.class);
            rule.setETag(JsonHelper.getNode(response, "ETag").asText());
            rule.setId(JsonHelper.getNode(response, "Key").asText());
            return rule;
        } catch (Exception e) {
            log.error("Could not parse data from Key Value Storage. " +
                "Json result: {}", jsonResultRule);
            throw new CompletionException(
                new ExternalDependencyException(
                    "Could not parse data from Key Value Storage"));
        }
    }
}
