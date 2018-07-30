// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.uiconfig.services.helpers.IHttpClientWrapper;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.IServicesConfig;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.concurrent.CompletionStage;

@Singleton
public class DeviceTelemetryClient implements IDeviceTelemetryClient {

    private IHttpClientWrapper httpClient;
    private String serviceUri;

    @Inject
    public DeviceTelemetryClient(
            IHttpClientWrapper httpClient,
            IServicesConfig config) {
        this.httpClient = httpClient;
        this.serviceUri = config.getTelemetryApiUrl();
    }

    @Override
    public CompletionStage updateRuleAsync(RuleApiModel rule, String etag) throws ExternalDependencyException {
        rule.setETag(etag);
        try {
            return this.httpClient.putAsync(String.format("%s/rules/%s", serviceUri, rule.getId()), String.format("Rule %s", rule.getId()), rule);
        } catch (URISyntaxException | UnsupportedEncodingException e) {
            throw new ExternalDependencyException("UpdateRule  failed");
        }
    }
}
