package com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.ExternalDependencyException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import play.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.CompletionException;

public class LogicApp implements IImplementation {
    private String endpointURL;
    private String solutionName;
    private String content;
    private List<String> email;
    private String ruleId;
    private String ruleDescription;
    private static final int LOGIC_OK = 202;
    private static final Logger.ALogger log = Logger.of(LogicApp.class);

    public LogicApp(String endpointURL, String solutionName) {
        this();
        this.endpointURL = endpointURL;
        this.solutionName = solutionName;
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
    public void execute(){
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(this.endpointURL);

        String json = this.generatePayLoad().toString();
        StringEntity entity;
        try {
            entity = new StringEntity(json);
        } catch (UnsupportedEncodingException e) {
            throw new CompletionException(
                    new ExternalDependencyException(e.getMessage()));
        }
        httpPost.setEntity(entity);
        httpPost.setHeader("Content-type", "application/json");

        try {
            CloseableHttpResponse response = client.execute(httpPost);
            int responseCode = response.getStatusLine().getStatusCode();
            if(responseCode != LOGIC_OK){
                log.error(String.format("Logic app error code %d", response.getStatusLine().getStatusCode()));
            }
        } catch (IOException e) {
            throw new CompletionException(
                    new ExternalDependencyException(e.getMessage()));

        }
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
