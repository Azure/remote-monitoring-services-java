// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.runtime;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidConfigurationException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.*;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.auth.ClientAuthConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.auth.IClientAuthConfig;
import com.typesafe.config.ConfigFactory;

import java.net.URL;
import java.net.URLDecoder;
import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;

public class Config implements IConfig {

    // Namespace applied to all the custom configuration settings
    private final String Namespace = "com.microsoft.azure.iotsolutions.";

    // Settings about this application
    private final String APPLICATION_KEY = Namespace + "telemetry.";

    // Storage dependency settings
    private final String COSMOS_DB_CONN_STRING_KEY = APPLICATION_KEY + "cosmosDb.connString";
    private final String BLOB_STORAGE_CONN_STRING_KEY = APPLICATION_KEY + "blobStorage.connString";
    private final String TIME_SERIES_KEY = APPLICATION_KEY + "timeSeriesInsights.";
    private final String TIME_SERIES_FQDN_KEY = TIME_SERIES_KEY + "fqdn";
    private final String AAD_TENANT_KEY = TIME_SERIES_KEY + "aadTenant";
    private final String AAD_APP_ID_KEY = TIME_SERIES_KEY + "aadAppId";
    private final String AAD_APP_SECRET_KEY = TIME_SERIES_KEY + "aadAppSecret";

    // Storage adapter webservice settings
    private final String KEY_VALUE_STORAGE_KEY = APPLICATION_KEY + "storageAdapter.";
    private final String KEY_VALUE_STORAGE_URL_KEY = KEY_VALUE_STORAGE_KEY + "url";

    private final String MESSAGES_STORAGE_TYPE_KEY = APPLICATION_KEY + "messages.storageType";
    private final String MESSAGES_COSMOS_DATABASE_KEY = APPLICATION_KEY + "messages.cosmosDb.database";
    private final String MESSAGES_COSMOS_COLLECTION_KEY = APPLICATION_KEY + "messages.cosmosDb.collection";
    private final String MESSAGES_TSI_API_VERSION_KEY = APPLICATION_KEY + "messages.timeSeriesInsights.apiVersion";
    private final String MESSAGES_TSI_DATE_FORMAT_KEY = APPLICATION_KEY + "messages.timeSeriesInsights.dateFormat";
    private final String MESSAGES_TSI_TIMEOUT_KEY = APPLICATION_KEY + "messages.timeSeriesInsights.timeOutInSeconds";
    private final String MESSAGES_TSI_AUTHORITY_URL_KEY = APPLICATION_KEY + "messages.timeSeriesInsights.authorityUrl";
    private final String MESSAGES_TSI_AUDIENCE_URL_KEY = APPLICATION_KEY + "messages.timeSeriesInsights.audienceUrl";
    private final String MESSAGES_TSI_EXPLORER_URL_KEY = APPLICATION_KEY + "messages.timeSeriesInsights.explorerUrl";

    private final String ALARMS_STORAGE_TYPE_KEY = APPLICATION_KEY + "alarms.storageType";
    private final String ALARMS_DOCDB_DATABASE_KEY = APPLICATION_KEY + "alarms.cosmosDb.database";
    private final String ALARMS_DOCDB_COLLECTION_KEY = APPLICATION_KEY + "alarms.cosmosDb.collection";
    private final String ALARMS_DOCDB_DELETE_RETRIES = APPLICATION_KEY + "alarms.cosmosDb.maxDeleteRetries";

    private final String ACTIONS_KEY = APPLICATION_KEY + "actions.";
    private final String ACTIONS_EVENTHUB_NAME_KEY = ACTIONS_KEY + "eventHubName";
    private final String ACTIONS_EVENTHUB_CONNECTION_STRING_KEY = ACTIONS_KEY + "eventHubConnectionString";
    private final String ACTIONS_EVENTHUB_OFFSET_TIME_IN_MINUTES_KEY = ACTIONS_KEY + "eventHubOffsetTimeInMinutes";
    private final String ACTIONS_EVENTHUB_CHECKPOINT_CONTAINER_KEY = ACTIONS_KEY + "eventHubCheckpointContainerName";
    private final String ACTIONS_LOGIC_APP_ENDPOINT_URL_KEY = ACTIONS_KEY + "logicAppEndPointUrl";
    private final String ACTIONS_SOLUTION_WEBSITE_URL_KEY = ACTIONS_KEY + "solutionWebsiteUrl";
    private final String ACTIONS_TEMPLATE_FOLDER_KEY = ACTIONS_KEY + "templateFolder";

    private final String CLIENT_AUTH_KEY = APPLICATION_KEY + "client-auth.";
    private final String AUTH_WEB_SERVICE_URL_KEY = CLIENT_AUTH_KEY + "auth_webservice_url";
    private final String AUTH_REQUIRED_KEY = CLIENT_AUTH_KEY + "auth_required";
    private final String AUTH_TYPE_KEY = CLIENT_AUTH_KEY + "auth_type";

    private final String JWT_KEY = APPLICATION_KEY + "client-auth.JWT.";
    private final String JWT_ALGOS_KEY = JWT_KEY + "allowed_algorithms";
    private final String JWT_ISSUER_KEY = JWT_KEY + "issuer";
    private final String JWT_AUDIENCE_KEY = JWT_KEY + "audience";
    private final String JWT_CLOCK_SKEW_KEY = JWT_KEY + "clock_skew_seconds";

    private final String DIAGNOSTICS_KEY = APPLICATION_KEY + "diagnostics.";
    private final String DIAGNOSTICS_URL_KEY = DIAGNOSTICS_KEY + "webservice_url";
    private final String DIAGNOSTICS_MAX_LOG_RETRIES = DIAGNOSTICS_KEY + "max_log_retries";

    private com.typesafe.config.Config data;
    private IServicesConfig servicesConfig;
    private IClientAuthConfig clientAuthConfig;

    @Inject
    public Config() {
        this.data = ConfigFactory.load();
    }

    /**
     * Service layer configuration
     */
    public IServicesConfig getServicesConfig() throws InvalidConfigurationException {
        if (this.servicesConfig != null) return this.servicesConfig;

        String storageConnectionString = this.data.getString(COSMOS_DB_CONN_STRING_KEY);
        String keyValueStorageUrl = this.data.getString(KEY_VALUE_STORAGE_URL_KEY);

        String messageStorageType = data.getString(MESSAGES_STORAGE_TYPE_KEY).toLowerCase();
        StorageConfig messagesStorageConfig = new StorageConfig(
            storageConnectionString,
            data.getString(MESSAGES_COSMOS_DATABASE_KEY),
            data.getString(MESSAGES_COSMOS_COLLECTION_KEY));

        TimeSeriesConfig timeSeriesConfig = null;
        if (messageStorageType.equalsIgnoreCase("tsi")) {
            timeSeriesConfig = new TimeSeriesConfig(
                data.getString(TIME_SERIES_FQDN_KEY),
                data.getString(AAD_TENANT_KEY),
                data.getString(AAD_APP_ID_KEY),
                data.getString(AAD_APP_SECRET_KEY),
                data.getString(MESSAGES_TSI_API_VERSION_KEY),
                data.getString(MESSAGES_TSI_AUTHORITY_URL_KEY),
                data.getString(MESSAGES_TSI_AUDIENCE_URL_KEY),
                data.getString(MESSAGES_TSI_EXPLORER_URL_KEY),
                data.getString(MESSAGES_TSI_DATE_FORMAT_KEY),
                data.getInt(MESSAGES_TSI_TIMEOUT_KEY));
        }

        MessagesConfig messagesConfig = new MessagesConfig(
            data.getString(MESSAGES_STORAGE_TYPE_KEY),
            messagesStorageConfig,
            timeSeriesConfig
        );

        StorageConfig alarmsStorageConfig = new StorageConfig(
            storageConnectionString,
            data.getString(ALARMS_DOCDB_DATABASE_KEY),
            data.getString(ALARMS_DOCDB_COLLECTION_KEY));

        AlarmsConfig alarmsConfig = new AlarmsConfig(
            data.getString(ALARMS_STORAGE_TYPE_KEY),
            alarmsStorageConfig,
            data.getInt(ALARMS_DOCDB_DELETE_RETRIES));

        ActionsConfig actionsConfig = new ActionsConfig(
            data.getString(ACTIONS_EVENTHUB_NAME_KEY),
            data.getString(ACTIONS_EVENTHUB_CONNECTION_STRING_KEY),
            data.getInt(ACTIONS_EVENTHUB_OFFSET_TIME_IN_MINUTES_KEY),
            data.getString(BLOB_STORAGE_CONN_STRING_KEY),
            data.getString(ACTIONS_EVENTHUB_CHECKPOINT_CONTAINER_KEY),
            validateUrl(data.getString(ACTIONS_LOGIC_APP_ENDPOINT_URL_KEY)),
            validateUrl(data.getString(ACTIONS_SOLUTION_WEBSITE_URL_KEY)),
            data.getString(ACTIONS_TEMPLATE_FOLDER_KEY));

        String diagnosticsUrl = "";
        if (data.hasPath(DIAGNOSTICS_URL_KEY)) {
            diagnosticsUrl = data.getString(DIAGNOSTICS_URL_KEY);
        }

        DiagnosticsConfig diagnosticsConfig = new DiagnosticsConfig(
            diagnosticsUrl,
            data.getInt(DIAGNOSTICS_MAX_LOG_RETRIES));

        this.servicesConfig = new ServicesConfig(
            keyValueStorageUrl,
            messagesConfig,
            alarmsConfig,
            actionsConfig,
            diagnosticsConfig);

        return this.servicesConfig;
    }

    /**
     * Client authorization configuration
     */
    public IClientAuthConfig getClientAuthConfig() {
        if (this.clientAuthConfig != null) return this.clientAuthConfig;

        // Default to True unless explicitly disabled
        Boolean authRequired = !data.hasPath(AUTH_REQUIRED_KEY)
            || data.getString(AUTH_REQUIRED_KEY).isEmpty()
            || data.getBoolean(AUTH_REQUIRED_KEY);

        String authServiceUrl = data.getString(AUTH_WEB_SERVICE_URL_KEY);

        // Default to JWT
        String authType = "JWT";
        if (data.hasPath(AUTH_REQUIRED_KEY)) {
            authType = data.getString(AUTH_TYPE_KEY);
        }

        // Default to RS256, RS384, RS512
        HashSet<String> jwtAllowedAlgos = new HashSet<>();
        jwtAllowedAlgos.add("RS256");
        jwtAllowedAlgos.add("RS384");
        jwtAllowedAlgos.add("RS512");
        if (data.hasPath(JWT_ALGOS_KEY)) {
            jwtAllowedAlgos.clear();
            Collections.addAll(
                jwtAllowedAlgos,
                data.getString(JWT_ALGOS_KEY).split(","));
        }

        // Default to empty, no issuer
        String jwtIssuer = "";
        if (data.hasPath(JWT_ISSUER_KEY)) {
            jwtIssuer = data.getString(JWT_ISSUER_KEY);
        }

        // Default to empty, no audience
        String jwtAudience = "";
        if (data.hasPath(JWT_AUDIENCE_KEY)) {
            jwtAudience = data.getString(JWT_AUDIENCE_KEY);
        }

        // Default to 2 minutes
        Duration jwtClockSkew = Duration.ofSeconds(120);
        if (data.hasPath(JWT_AUDIENCE_KEY)) {
            jwtClockSkew = data.getDuration(JWT_CLOCK_SKEW_KEY);
        }

        this.clientAuthConfig = new ClientAuthConfig(
            authRequired,
            authServiceUrl,
            authType,
            jwtAllowedAlgos,
            jwtIssuer,
            jwtAudience,
            jwtClockSkew);

        return this.clientAuthConfig;
    }

    /**
     * Validate Url format and return same value if passed
     * @param url to be validated against URL format
     * @return the same value of input
     * @throws {@link InvalidConfigurationException}
     */
    private String validateUrl(String url) throws InvalidConfigurationException {
        try {
            new URL(url);
            // Decode the url since WSClient might encode the url into incorrect format
            return URLDecoder.decode(url, "UTF-8");
        } catch (Exception e) {
            throw new InvalidConfigurationException(String.format("Malformed Url: %s", url), e);
        }
    }
}

