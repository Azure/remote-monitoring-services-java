// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.webservice.runtime;

import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.ServicesConfig;
import com.microsoft.azure.iotsolutions.uiconfig.webservice.auth.ClientAuthConfig;
import com.microsoft.azure.iotsolutions.uiconfig.webservice.auth.IClientAuthConfig;
import com.typesafe.config.ConfigFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;

public class Config implements IConfig {

    private final String NAMESPACE = "com.microsoft.azure.iotsolutions.";
    private final String APPLICATION_KEY = NAMESPACE + "uiconfig.";
    private final String STORAGE_ADAPTER_WEBSERVICE_URL = APPLICATION_KEY + "storageadapter-webservice-url";
    private final String DEVICESIMULATION_WEBSERVICE_URL = APPLICATION_KEY + "devicesimulation-webservice-url";
    private final String IOTHUBMANAGER_WEBSERVICE_URL = APPLICATION_KEY + "iothubmanager-webservice-url";
    private final String TELEMETRY_WEBSERVICE_URL = APPLICATION_KEY + "telemetry-webservice-url";
    private final String CACHE_TTL = APPLICATION_KEY + "cache_TTL";
    private final String REBUILD_TIMEOUT = APPLICATION_KEY + "rebuild_timeout";
    private final String SEED_TEMPLATEKEY = APPLICATION_KEY + "seed-template";
    private final String BINGMAP_KEY = APPLICATION_KEY + "bingmap-key";
    private final String CACHE_WHITELIST_KEY = APPLICATION_KEY + "cache_whitelist";

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
        // Load `application.conf` and replace placeholders with
        // environment variables
        this.data = ConfigFactory.load();
    }

    /// <summary>Service layer configuration</summary>
    public IServicesConfig getServicesConfig() {
        if (this.servicesConfig != null) return this.servicesConfig;
        this.servicesConfig = new ServicesConfig(this.data.getString(TELEMETRY_WEBSERVICE_URL), this.data.getString(STORAGE_ADAPTER_WEBSERVICE_URL),
                this.data.getString(DEVICESIMULATION_WEBSERVICE_URL),
                this.data.getString(IOTHUBMANAGER_WEBSERVICE_URL),
                this.data.getInt(CACHE_TTL),
                this.data.getInt(REBUILD_TIMEOUT),
                this.data.getString(SEED_TEMPLATEKEY),
                this.data.getString(BINGMAP_KEY),
                this.data.getStringList(CACHE_WHITELIST_KEY));
        return servicesConfig;
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
