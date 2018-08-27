// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime;

import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidConfigurationException;
import play.Logger;

public class TimeSeriesConfig {

    private static final Logger.ALogger log = Logger.of(TimeSeriesConfig.class);
    private static final String TSI_DEFAULT_AUTHORITY_URL = "https://login.windows.net/";
    private static final String TSI_DEFAULT_AUDIENCE_URL = "https://api.timeseries.azure.com/";
    private static final String TSI_DEFAULT_EXPLORER_URL = "https://insights.timeseries.azure.com/";

    private final String dataAccessFqdn;
    private final String aadTenant;
    private final String aadApplicationId;
    private final String aadApplicationSecret;
    private final String apiVersion;
    private final String authorityUrl;
    private final String audienceUrl;
    private final String explorerUrl;
    private final String dateFormat;
    private final int timeOutInSeconds;

    private final int DEFAULT_TIMEOUT_IN_SECONDS = 20;

    public TimeSeriesConfig(
        String dataAccessFqdn,
        String aadTenant,
        String aadApplicationId,
        String aadApplicationSecret,
        String apiVersion,
        String authorityUrl,
        String audienceUrl,
        String explorerUrl,
        String dateFormat,
        int timeOutInSeconds) throws InvalidConfigurationException {

        this.dataAccessFqdn = dataAccessFqdn;
        if (this.dataAccessFqdn == null || this.dataAccessFqdn.isEmpty()) {
            this.logErrorAndThrowException("Time Series data access FQDN setting is empty");
        }

        this.aadTenant = aadTenant;
        if (this.aadTenant == null || this.aadTenant.isEmpty()) {
            this.logErrorAndThrowException("AAD tenant setting is empty");
        }

        this.aadApplicationId = aadApplicationId;
        if (this.aadApplicationId == null || this.aadApplicationId.isEmpty()) {
            this.logErrorAndThrowException("AAD application id setting is empty");
        }

        this.aadApplicationSecret = aadApplicationSecret;
        if (this.aadApplicationSecret == null || this.aadApplicationSecret.isEmpty()) {
            logErrorAndThrowException("AAD application secret setting is empty");
        }

        this.apiVersion = apiVersion;
        if (this.apiVersion == null || this.apiVersion.isEmpty()) {
            this.logErrorAndThrowException("Time Series API version setting is empty");
        }

        this.authorityUrl = (authorityUrl == null && authorityUrl.isEmpty()) ? TSI_DEFAULT_AUTHORITY_URL : authorityUrl;
        this.audienceUrl = (audienceUrl == null && audienceUrl.isEmpty()) ? TSI_DEFAULT_AUDIENCE_URL : audienceUrl;
        this.explorerUrl = (explorerUrl == null && explorerUrl.isEmpty()) ? TSI_DEFAULT_EXPLORER_URL : explorerUrl;

        this.dateFormat = dateFormat;
        if (this.dateFormat == null || this.dateFormat.isEmpty()) {
            this.logErrorAndThrowException("Time Series date format setting is empty");
        }

        this.timeOutInSeconds = timeOutInSeconds > 0 ? timeOutInSeconds : DEFAULT_TIMEOUT_IN_SECONDS;
    }

    public String getDataAccessFqdn() {
        return this.dataAccessFqdn;
    }

    public String getAuthorityUrl() {
        return authorityUrl;
    }

    public String getAudienceUrl() {
        return audienceUrl;
    }

    public String getExplorerUrl() {
        String environmentId = this.dataAccessFqdn.substring(0, this.dataAccessFqdn.indexOf("."));
        return String.format("%s?environmentId=%s&tid=%s", this.explorerUrl, environmentId, this.aadTenant);
    }

    public String getAadTenant() {
        return this.aadTenant;
    }

    public String getAadApplicationId() {
        return this.aadApplicationId;
    }

    public String getAadApplicationSecret() {
        return this.aadApplicationSecret;
    }

    public String getApiVersion() {
        return this.apiVersion;
    }

    public String getDateFormat() {
        return this.dateFormat;
    }

    public int getTimeOutInSeconds() {
        return this.timeOutInSeconds;
    }

    private void logErrorAndThrowException(String message)
        throws InvalidConfigurationException {
        log.error(message);
        throw new InvalidConfigurationException(message);
    }
}
