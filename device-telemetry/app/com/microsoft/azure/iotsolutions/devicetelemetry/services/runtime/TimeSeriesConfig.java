// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime;

import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidConfigurationException;
import play.Logger;

public class TimeSeriesConfig {

    private static final Logger.ALogger log = Logger.of(TimeSeriesConfig.class);
    private static final String TSI_EXPLORER_URL_FORMAT = "https://insights.timeseries.azure.com/?environmentId=%s&tid=%s";

    private final String timeSeriesFqdn;
    private final String aadTenant;
    private final String aadApplicationId;
    private final String aadApplicationSecret;
    private final String apiVersion;
    private final String dateFormat;
    private final int timeOutInSeconds;

    private final int DEFAULT_TIMEOUT_IN_SECONDS = 20;

    public TimeSeriesConfig(
        String timeSeriesFqdn,
        String aadTenant,
        String aadApplicationId,
        String aadApplicationSecret,
        String apiVersion,
        String dateFormat,
        int timeOutInSeconds) throws InvalidConfigurationException {

        this.timeSeriesFqdn = timeSeriesFqdn;
        if (this.timeSeriesFqdn == null || this.timeSeriesFqdn.isEmpty()) {
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
        if (this.apiVersion == null && this.apiVersion.isEmpty()) {
            this.logErrorAndThrowException("Time Series API version setting is empty");
        }

        this.dateFormat = dateFormat;
        if (this.dateFormat == null || this.dateFormat.isEmpty()) {
            this.logErrorAndThrowException("Time Series date format setting is empty");
        }

        this.timeOutInSeconds = timeOutInSeconds > 0 ? timeOutInSeconds : DEFAULT_TIMEOUT_IN_SECONDS;
    }

    public String getTimeSeriesFqdn() {
        return this.timeSeriesFqdn;
    }

    public String getTimeSeriesExplorerUrl() {
        String environmentId = this.timeSeriesFqdn.substring(0, this.timeSeriesFqdn.indexOf("."));
        return String.format(TSI_EXPLORER_URL_FORMAT, environmentId, this.aadTenant);
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
