// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.actions;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.models.AsaAlarmApiModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.actionsagent.models.EmailActionPayload;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.ResourceNotFoundException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.EmailActionServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.ActionsConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServicesConfig;
import org.apache.http.HttpStatus;
import org.joda.time.DateTime;
import play.Logger;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;

public class EmailActionExecutor implements IActionExecutor {

    private String logicAppEndpointUrl;
    private String solutionWebsiteUrl;
    private String templatePath;
    private String emailTemplate;
    private WSClient wsClient;

    private static final String EMAIL_TEMPLATE_FILE_NAME = "EmailTemplate.html";
    private static final String DATE_FORMAT_STRING = "E, dd MMM yyyy HH:mm:ss z";
    private static final Logger.ALogger log = Logger.of(EmailActionExecutor.class);

    @Inject
    public EmailActionExecutor(final IServicesConfig serviceConfig, final WSClient wsClient) throws ResourceNotFoundException {
        ActionsConfig actionsConfig = serviceConfig.getActionsConfig();
        this.logicAppEndpointUrl = actionsConfig.getLogicAppEndpointUrl();
        this.solutionWebsiteUrl = actionsConfig.getSolutionWebsiteUrl();
        this.templatePath = actionsConfig.getTemplateFolder();
        this.wsClient = wsClient;
        this.emailTemplate = loadEmailTemplate(String.format("/resources/%s/%s",
            this.templatePath, EMAIL_TEMPLATE_FILE_NAME));
    }

    /**
     * Execute the given email action for the given alarm.
     * Sends a post request to Logic App with alarm information
     *
     * @param emailAction send an email defined by action
     * @param alarm       to trigger email notification
     * @return CompletionStage
     * @throws {@link ResourceNotFoundException}
     */
    public CompletionStage execute(EmailActionServiceModel emailAction, AsaAlarmApiModel alarm) {
        String content = this.generatePayload(emailAction, alarm);
        return this.prepareRequest()
            .post(content)
            .handle((result, error) -> {
                if (isSuccess(result.getStatus())) {
                    String msg = String.format("Could not execute email action against logic app: %s", result.getStatusText());
                    log.error(msg);
                    throw new CompletionException(new ExternalDependencyException(msg));
                }

                if (error != null) {
                    String msg = String.format("Could not connect to logic app", error.getMessage());
                    log.error(msg);
                    throw new CompletionException(new ExternalDependencyException(msg));
                }

                return CompletableFuture.completedFuture(true);
            });
    }

    private WSRequest prepareRequest() {
        WSRequest wsRequest = this.wsClient
            .url(this.logicAppEndpointUrl)
            .addHeader("Csrf-Token", "no-check")
            .addHeader("Content-Type", "application/json");

        return wsRequest;
    }

    private Boolean isSuccess(int statusCode) {
        return statusCode >= HttpStatus.SC_OK && statusCode < HttpStatus.SC_MULTIPLE_CHOICES;
    }

    private String generatePayload(EmailActionServiceModel emailAction, AsaAlarmApiModel alarm) {
        DateTime alarmDate = new DateTime(Long.parseLong(alarm.getDateCreated()));
        String emailBody = this.emailTemplate.replace("${subject}", emailAction.getSubject())
            .replace("${notes}", emailAction.getNotes())
            .replace("${alarmDate}", alarmDate.toString(DATE_FORMAT_STRING))
            .replace("${ruleId}", alarm.getRuleId())
            .replace("${ruleDescription}", alarm.getRuleDescription())
            .replace("${ruleSeverity}", alarm.getRuleSeverity())
            .replace("${deviceId}", alarm.getDeviceId())
            .replace("${alarmUrl}", this.generateRuleDetailUrl(alarm.getRuleId()));

        EmailActionPayload payload = new EmailActionPayload(
            emailAction.getRecipients(),
            emailAction.getSubject(),
            emailBody
        );

        return Json.stringify(Json.toJson(payload));
    }

    private String loadEmailTemplate(String path) throws ResourceNotFoundException {
        String emailTemplate = "";
        try (InputStream is = this.getClass().getResourceAsStream(path)) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
                String line = reader.readLine();
                while (line != null) {
                    emailTemplate += line;
                    line = reader.readLine();
                }
            }
        } catch (Exception e) {
            String message = String.format("Email template %s does not exist", templatePath);
            log.error(message, e);
            throw new ResourceNotFoundException(message, e);
        }
        return emailTemplate;
    }

    private String generateRuleDetailUrl(String ruleId) {
        return String.format("%s/maintenance/rule/%s", this.solutionWebsiteUrl, ruleId);
    }
}

