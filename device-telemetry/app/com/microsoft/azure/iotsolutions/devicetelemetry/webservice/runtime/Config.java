// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.webservice.runtime;

import com.microsoft.azure.eventprocessorhost.IEventProcessorFactory;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.EventProcessorHostWrapper;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.IEventProcessorHostWrapper;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.notification.NotificationEventProcessorFactory;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.*;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.auth.ClientAuthConfig;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.auth.IClientAuthConfig;
import com.typesafe.config.ConfigFactory;
import play.api.Logger;

import java.io.IOException;
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
    private final String ALARMS_DOCDB_DELETE_RETRIES = APPLICATION_KEY + "alarms.documentDb.maxDeleteRetries";


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
    private IBlobStorageConfig blobStorageConfig;
    private IClientAuthConfig clientAuthConfig;
    private IEventProcessorHostWrapper eventProcessorHostWrapper;
    private IEventProcessorFactory eventProcessorFactory;

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

        AlarmsConfig alarmsConfig = new AlarmsConfig(
            data.getString(ALARMS_STORAGE_TYPE_KEY).toLowerCase(),
            data.getString(ALARMS_DOCDB_CONN_STRING_KEY),
            data.getString(ALARMS_DOCDB_DATABASE_KEY),
            data.getString(ALARMS_DOCDB_COLLECTION_KEY),
            data.getInt(ALARMS_DOCDB_DELETE_RETRIES));

        // temporary solution: fill in manually
        String eventHubName = "notificationsystem";
        String logicAppEndPointUrl = "https://prod-00.southeastasia.logic.azure.com:443/workflows/1f2493004aea43e1ac661f071a15f330/triggers/manual/paths/invoke?api-version=2016-10-01&sp=%2Ftriggers%2Fmanual%2Frun&sv=1.0&sig=DIfPL17M7qydXwHxD7g-_K-P3mE6dqYuv7aDfbQji94";
        String eventHubConnectionString = "Endpoint=sb://eventhubnamespace-f3pvd.servicebus.windows.net/;SharedAccessKeyName=NotificationSystem;SharedAccessKey=W8C1Y/ZoBglooXxc1O1r2y5QBl7sa0nIwrYRl5h5YhA=;EntityPath=notificationsystem";
        int eventHubOffsetTimeInMinutes = 0;

        this.servicesConfig = new ServicesConfig(
            storageConnectionString,
            keyValueStorageUrl,
            messagesConfig,
            alarmsConfig,
            eventHubName,
            eventHubConnectionString,
            eventHubOffsetTimeInMinutes,
            logicAppEndPointUrl);

        return this.servicesConfig;
    }

    @Override
    public IBlobStorageConfig getBlobStorageConfig(){
        if (this.blobStorageConfig != null) return this.blobStorageConfig;
        // temp fill manually

         /*private String EhConnectionString = "Endpoint=sb://eventhubnamespace-f3pvd.servicebus.windows.net/;SharedAccessKeyName=NotificationSystem;SharedAccessKey=W8C1Y/ZoBglooXxc1O1r2y5QBl7sa0nIwrYRl5h5YhA=;EntityPath=notificationsystem";
        private String EhEntityPath = "notificationsystem";
        private String StorageContainerName = "anothersystem";
        private String StorageAccountName = "aayushdemo";
        private String StorageAccountKey = "qIFS9KOWkR+GUymNElgeGGQhwvATW5SNRii4R4OTWYi0aiT/JrIFnnLyJlUVigyIoNzr5TR9utGwZoK2ffioAw==";
        private String StorageConnectionString = String.format("DefaultEndpointsProtocol=https;AccountName=%s;AccountKey=%s", StorageAccountName, StorageAccountKey);*/

         String accountKey = "qIFS9KOWkR+GUymNElgeGGQhwvATW5SNRii4R4OTWYi0aiT/JrIFnnLyJlUVigyIoNzr5TR9utGwZoK2ffioAw==";
         String accountName = "aayushdemo";
         String endpointSuffix = "core.windows.net";
         String eventHubContainer = "notification-sstem";

         this.blobStorageConfig = new BlobStorageConfig(accountName, accountKey, endpointSuffix, eventHubContainer);
         return this.blobStorageConfig;
    }

    @Override
    public IEventProcessorHostWrapper getEventProcessorHostWrapper() {
        if (this.eventProcessorHostWrapper != null) return this.eventProcessorHostWrapper;

        this.eventProcessorHostWrapper = new EventProcessorHostWrapper();
        return this.eventProcessorHostWrapper;
    }

    @Override
    public IEventProcessorFactory getEventProcessorFactory() {
        if (this.eventProcessorFactory != null) return this.eventProcessorFactory;

        this.eventProcessorFactory = new NotificationEventProcessorFactory(this.getServicesConfig());
        return this.eventProcessorFactory;
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
