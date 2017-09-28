// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.runtime;

import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.ServicesConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.StorageConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.auth.ClientAuthConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.auth.IClientAuthConfig;
import com.typesafe.config.ConfigFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;

// TODO: documentation
// TODO: handle exceptions

public class Config implements IConfig {

    // Namespace applied to all the custom configuration settings
    private final String Namespace = "com.microsoft.azure.iotsolutions.";

    // Settings about this application
    private final String APPLICATION_KEY = Namespace + "telemetry.";

    // Storage dependency settings
    private final String STORAGE_KEY = APPLICATION_KEY + "documentdb.";
    private final String STORAGE_CONN_STRING_KEY = STORAGE_KEY + "connstring";

    // Storage adapter webservice settings
    private final String KEY_VALUE_STORAGE_KEY = APPLICATION_KEY + "storageadapter.";
    private final String KEY_VALUE_STORAGE_URL_KEY = KEY_VALUE_STORAGE_KEY + "url";

    private final String MESSAGES_STORAGE_TYPE_KEY = APPLICATION_KEY + "messages.storageType";
    private final String MESSAGES_DOCDB_CONN_STRING_KEY = APPLICATION_KEY + "messages.documentDb.connString";
    private final String MESSAGES_DOCDB_DATABASE_KEY = APPLICATION_KEY + "messages.documentDb.database";
    private final String MESSAGES_DOCDB_COLLECTION_KEY = APPLICATION_KEY + "messages.documentDb.collection";

    private final String ALARMS_STORAGE_TYPE_KEY = APPLICATION_KEY + "alarms.storageType";
    private final String ALARMS_DOCDB_CONN_STRING_KEY = APPLICATION_KEY + "alarms.documentDb.connString";
    private final String ALARMS_DOCDB_DATABASE_KEY = APPLICATION_KEY + "alarms.documentDb.database";
    private final String ALARMS_DOCDB_COLLECTION_KEY = APPLICATION_KEY + "alarms.documentDb.collection";

    private final String CLIENT_AUTH_KEY = APPLICATION_KEY + "client-auth.";
    private final String AUTH_REQUIRED_KEY = CLIENT_AUTH_KEY + "auth_required";
    private final String AUTH_TYPE_KEY = CLIENT_AUTH_KEY + "auth_type";

    private final String JWT_KEY = APPLICATION_KEY + "client-auth.JWT.";
    private final String JWT_ALGOS_KEY = JWT_KEY + "allowed_algorithms";
    private final String JWT_ISSUER_KEY = JWT_KEY + "issuer";
    private final String JWT_AUDIENCE_KEY = JWT_KEY + "audience";
    private final String JWT_CLOCK_SKEW_KEY = JWT_KEY + "clock_skew_seconds";

    private com.typesafe.config.Config data;
    private IServicesConfig servicesConfig;
    private IClientAuthConfig clientAuthConfig;

    public Config() {
        this.data = ConfigFactory.load();
    }

    /**
     * Service layer configuration
     */
    public IServicesConfig getServicesConfig() {

        if (this.servicesConfig != null) return this.servicesConfig;

        String storageConnectionString = this.data.getString(STORAGE_CONN_STRING_KEY);
        String keyValueStorageUrl = this.data.getString(KEY_VALUE_STORAGE_URL_KEY);

        StorageConfig messagesConfig = new StorageConfig(
            data.getString(MESSAGES_STORAGE_TYPE_KEY).toLowerCase(),
            data.getString(MESSAGES_DOCDB_CONN_STRING_KEY),
            data.getString(MESSAGES_DOCDB_DATABASE_KEY),
            data.getString(MESSAGES_DOCDB_COLLECTION_KEY));

        StorageConfig alarmsConfig = new StorageConfig(
            data.getString(ALARMS_STORAGE_TYPE_KEY).toLowerCase(),
            data.getString(ALARMS_DOCDB_CONN_STRING_KEY),
            data.getString(ALARMS_DOCDB_DATABASE_KEY),
            data.getString(ALARMS_DOCDB_COLLECTION_KEY));

        this.servicesConfig = new ServicesConfig(
            storageConnectionString,
            keyValueStorageUrl,
            messagesConfig,
            alarmsConfig);

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
            authRequired, authType, jwtAllowedAlgos, jwtIssuer, jwtAudience, jwtClockSkew);

        return this.clientAuthConfig;
    }
}
