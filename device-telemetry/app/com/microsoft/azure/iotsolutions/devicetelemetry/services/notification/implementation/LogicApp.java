package com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.ExternalDependencyException;
import play.Logger;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;

public class LogicApp implements IImplementation {
    private String endpointURL;
    private String solutionName;
    private String content;
    private List<String> email;
    private String ruleId;
    private String ruleDescription;
    private static final int CONFLICT = 409;
    private static final int NOT_FOUND = 404;
    private static final int OK = 200;
    private static final Logger.ALogger log = Logger.of(LogicApp.class);

    private WSClient wsClient;

    @Inject
    public LogicApp(String endpointURL, String solutionName, final WSClient wsClient) {
        this();
        this.endpointURL = endpointURL;
        this.solutionName = solutionName;
        this.wsClient = wsClient;
    }

    public LogicApp() {
        this.content = "";
        this.ruleId = "";
        this.ruleDescription = "";
    }

    @Override
    public void setReceiver(List<String> receiver) {
        this.email = receiver;
    }

    @Override
    public void setMessage(String message, String ruleId, String ruleDescription) {
        this.content = message;
        this.ruleId = ruleId;
        this.ruleDescription = ruleDescription;
    }

    @Override
    public CompletionStage execute() {
        ObjectNode jsonData = this.generatePayLoad();
        return this.prepareRequest()
            .post(jsonData.toString())
                .handle((result, error) -> {
                    if (result.getStatus() != OK) {
                        log.error("Logic app error code {}",
                            result.getStatusText());
                        throw new CompletionException(
                            new ExternalDependencyException(result.getStatusText()));
                    }

                    if (error != null) {
                        log.error("Logic app request error: {}",
                            error.getMessage());
                        throw new CompletionException(
                            new ExternalDependencyException(
                                "Could not connect to logic app " +
                                    error.getMessage()));
                    }

                    return CompletableFuture.completedFuture(true);
                });
    }

    private WSRequest prepareRequest() {
        String url = this.endpointURL;
                //"https://prod-14.southeastasia.logic.azure.com:443/workflows/49567a8fac3e49aa8ac72a6472a1da64/triggers/manual/paths/invoke?api-version=2016-10-01&sp=%2Ftriggers%2Fmanual%2Frun&sv=1.0&sig=1gmlRlfj0h-z2iJ6Q4nEkw68WaaN6DQj3iknrboJddg";
        WSRequest wsRequest = this.wsClient
                .url(url)
                .addHeader("Csrf-Token", "no-check")
                .addHeader("Content-Type", "application/json");

        return wsRequest;
    }

    private String generateRuleDetailUrl() {
        return String.format("https://%s.azurewebsites.net/maintenance/rule/%s", this.solutionName, this.ruleId);
    }

    private ObjectNode generatePayLoad() {
        String emailContent = String.format("Alarm fired for rule ID %s: %s. Custom message: %s. Alarm detail page: %s", this.ruleId, this.ruleDescription, this.content, this.generateRuleDetailUrl());
        if (this.email == null || this.content == null) {
            throw new IllegalArgumentException("No email receiver or content provided");
        }

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonData = mapper.createObjectNode();
        ArrayNode array = mapper.createArrayNode();

        this.email.stream().forEach(e -> array.add(e));

        jsonData.putPOJO("emailAddress", array);
        jsonData.put("template", emailContent);
        return jsonData;
    }
}
