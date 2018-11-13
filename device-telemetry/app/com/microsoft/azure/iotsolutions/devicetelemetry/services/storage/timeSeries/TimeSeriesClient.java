// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services.storage.timeSeries;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import com.microsoft.aad.adal4j.*;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.*;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.MessageListServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.StatusResultServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.runtime.*;
import org.apache.http.HttpStatus;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import play.Logger;
import play.libs.Json;
import play.libs.ws.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class TimeSeriesClient implements ITimeSeriesClient {

    private static final Logger.ALogger log = Logger.of(TimeSeriesConfig.class);
    public static final int CLOCK_CALIBRATION_IN_SECONDS = 5;

    private final WSClient wsClient;

    private final String AVAILABILITY_KEY = "availability";
    private final String EVENTS_KEY = "events";
    private final String SEARCH_SPAN_KEY = "searchSpan";
    private final String PREDICATE_KEY = "predicate";
    private final String PREDICATE_STRING_KEY = "predicateString";
    private final String TOP_KEY = "top";
    private final String SORT_KEY = "sort";
    private final String SORT_INPUT_KEY = "input";
    private final String BUILT_IN_PROP_KEY = "builtInProperty";
    private final String BUILT_IN_PROP_VALUE = "$ts";
    private final String SORT_ORDER_KEY = "order";
    private final String COUNT_KEY = "count";
    private final String FROM_KEY = "from";
    private final String TO_KEY = "to";
    private final String DEVICE_ID_KEY = "iothub-connection-device-id";

    private String aadTenantId;
    private String applicationId;
    private String applicationSecret;
    private String dataAccessFqdn;
    private String apiVersion;
    private String authorityUrl;
    private String audienceUrl;
    private String dateFormat;
    private int timeoutInSeconds;

    private static AuthenticationResult authenticationResult;

    @Inject
    public TimeSeriesClient(IServicesConfig config, WSClient wsClient)
        throws InvalidConfigurationException {
        this.wsClient = wsClient;

        MessagesConfig messagesConfig = config.getMessagesConfig();
        if (messagesConfig == null) {
            throw new InvalidConfigurationException("Message config is empty");
        }

        TimeSeriesConfig tsiConfig = messagesConfig.getTimeSeriesConfig();

        if (tsiConfig != null) {
            this.dataAccessFqdn = tsiConfig.getDataAccessFqdn();
            this.aadTenantId = tsiConfig.getAadTenant();
            this.applicationId = tsiConfig.getAadApplicationId();
            this.applicationSecret = tsiConfig.getAadApplicationSecret();
            this.apiVersion = tsiConfig.getApiVersion();
            this.authorityUrl = tsiConfig.getAuthorityUrl();
            this.audienceUrl = tsiConfig.getAuthorityUrl();
            this.audienceUrl = tsiConfig.getAudienceUrl();
            this.dateFormat = tsiConfig.getDateFormat();
            this.timeoutInSeconds = tsiConfig.getTimeOutInSeconds();
        } else {
            log.info("Time Series config is empty");
            return;
        }
    }

    @Override
    public StatusResultServiceModel ping() {
        StatusResultServiceModel result = new StatusResultServiceModel(false, "TimeSeriesInsights check failed");

        try {
            WSRequest request = this.PrepareRequest(
                this.dataAccessFqdn,
                AVAILABILITY_KEY,
                this.acquireAccessToken());

            WSResponse response = request.get().toCompletableFuture().get();

            if (response.getStatus() != HttpStatus.SC_OK) {
                result.setMessage("Status code: " + response.getStatus() + ", Response: " + response.getBody());
            } else {
                result.setIsHealthy(true);
                result.setMessage("Alive and well!");
            }
        } catch (Exception e) {
            this.log.error(e.getMessage());
        }

        return result;
    }

    @Override
    public MessageListServiceModel queryEvents(
        DateTime from,
        DateTime to,
        String order,
        int skip,
        int limit,
        String[] deviceIds) throws TimeSeriesParseException, InvalidConfigurationException {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode queryObjectNode = mapper.createObjectNode();

        if (from == null) {
            from = new DateTime(0).toDateTime(DateTimeZone.UTC);
        }
        if (to == null) {
            to = DateTime.now().toDateTime(DateTimeZone.UTC);
        }

        queryObjectNode.putObject(SEARCH_SPAN_KEY)
            .put(FROM_KEY, from.toString(this.dateFormat))
            .put(TO_KEY, to.toString(this.dateFormat));

        if (deviceIds != null && deviceIds.length > 0) {
            List<String> devicePredicates = Arrays.asList(deviceIds).stream()
                .map(id -> String.format("[%s].String='%s'", DEVICE_ID_KEY, id))
                .collect(Collectors.toList());
            queryObjectNode.putObject(PREDICATE_KEY)
                .put(PREDICATE_STRING_KEY, String.join(" OR ", devicePredicates));
        }

        ObjectNode topObject = queryObjectNode.putObject(TOP_KEY);
        ObjectNode sortObject = mapper.createObjectNode();

        sortObject.putObject(SORT_INPUT_KEY)
            .put(BUILT_IN_PROP_KEY, BUILT_IN_PROP_VALUE);
        sortObject.put(SORT_ORDER_KEY, order);

        topObject.putArray(SORT_KEY)
            .add(sortObject);
        topObject.put(COUNT_KEY, skip + limit);

        try {
            WSRequest request = this.PrepareRequest(
                this.dataAccessFqdn,
                EVENTS_KEY,
                this.acquireAccessToken());

            WSResponse response = request
                .post(queryObjectNode.toString())
                .toCompletableFuture()
                .get();

            EventListApiModel valueList = Json.fromJson(Json.parse(response.getBody()),
                EventListApiModel.class);

            return valueList.toMessageList(skip);
        } catch (TimeSeriesParseException e) {
            String errorMessage = "Failed to parse events from Time Series Insights";
            log.error(errorMessage, e);
            throw e;
        } catch (Exception e) {
            String errorMessage = "Failed to query Time Series Insights";
            log.error(errorMessage, e);
            throw new InvalidConfigurationException(errorMessage, e);
        }
    }

    private String acquireAccessToken() throws Exception {
        AuthenticationContext context;
        AuthenticationResult result;
        ExecutorService service = null;

        if (this.authenticationResult != null) {
            Instant expiredTime = Instant.ofEpochMilli(this.authenticationResult.getExpiresOnDate().getTime());
            // add a few seconds to calibrate clock drift
            Instant futureTime = Instant.now().plusSeconds(CLOCK_CALIBRATION_IN_SECONDS);
            if (expiredTime.isAfter(futureTime)) {
                return this.authenticationResult.getAccessToken();
            }
        }

        try {
            service = Executors.newFixedThreadPool(1);
            context = new AuthenticationContext(
                String.format("%s%s", this.authorityUrl, this.aadTenantId),
                false, service);
            Future<AuthenticationResult> future = context.acquireToken(
                this.audienceUrl,
                new ClientCredential(this.applicationId, this.applicationSecret),
                null
            );
            result = future.get();
        } catch (Exception e) {
            throw new NotAuthorizedException("Unable to acquire access token");
        } finally {
            service.shutdown();
        }

        if (result == null) {
            throw new NotAuthorizedException("AAD authentication result was null");
        }

        this.authenticationResult = result;
        return this.authenticationResult.getAccessToken();
    }

    private WSRequest PrepareRequest(
        String fqdn,
        String path,
        String accessToken) {

        String url = String.format("https://%s/%s?api-version=%s&timeout=PT%sS",
            fqdn, path, this.apiVersion, this.timeoutInSeconds);

        WSRequest request = this.wsClient.url(url)
            .addHeader("x-ms-client-application-name", this.applicationId)
            .addHeader("Authorization", "Bearer " + accessToken);

        return request;
    }
}
