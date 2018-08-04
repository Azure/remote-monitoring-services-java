// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.*;
import com.microsoft.azure.iotsolutions.iothubmanager.services.external.IStorageAdapterClient;
import com.microsoft.azure.iotsolutions.iothubmanager.services.external.ValueApiModel;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DevicePropertyServiceModel;
import com.microsoft.azure.iotsolutions.iothubmanager.services.runtime.IServicesConfig;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import play.Logger;
import play.libs.Json;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @summary This class creates/reads cache of deviceProperties in/from CosmosDB.
 * @remarks This is done to avoid request throttling when deviceProperties are queried directly from IOT-Hub.
 * This class is called "deviceProperties" even though it deals with both properties
 * and tags of devices.
 **/
@Singleton
public class DeviceProperties implements IDeviceProperties {

    private final IStorageAdapterClient storageClient;
    private static final Logger.ALogger log = Logger.of(DeviceProperties.class);
    // Hardcoded in application.conf
    private final int TTL;
    private final String CacheCollectionId = "cache";
    private final String CacheKey = "device-properties";
    // Hardcoded in application.conf
    private final List<String> whitelist;
    private static final String WHITELIST_TAG_PREFIX = "tags.";
    private static final String WHITELIST_REPORTED_PREFIX = "reported.";
    private static final long SERVICE_QUERY_INTERVAL_MS = 10000;
    private static final long SERVICE_QUERY_INTERVAL_SECS = SERVICE_QUERY_INTERVAL_MS / 1000;
    private final IDevices devices;
    private final String TAG_PREFIX = "Tags.";
    private final String REPORTED_PREFIX = "Properties.Reported.";

    /**
     * @summary Class Constructor
     */
    @Inject
    public DeviceProperties(IStorageAdapterClient storageClient,
                            IServicesConfig config,
                            IDevices devices) throws ExternalDependencyException {
        this.storageClient = storageClient;
        this.TTL = config.getDevicePropertiesTTL();
        this.whitelist = config.getDevicePropertiesWhiteList();
        this.devices = devices;
    }

    /**
     * @summary Get List of deviceProperties from cache
     */
    @Override
    public CompletionStage<TreeSet<String>> getListAsync() throws
        ResourceNotFoundException,
        ConflictingResourceException,
        ExternalDependencyException,
        InvalidInputException {
        ValueApiModel valueApiModel = new ValueApiModel();
        try {
            valueApiModel = storageClient.getAsync(CacheCollectionId, CacheKey).
                toCompletableFuture().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new ExternalDependencyException("Unable to get deviceProperties cache from storage.");
        }
        DevicePropertyServiceModel devicePropertyServiceModel = new DevicePropertyServiceModel();
        try {
            devicePropertyServiceModel = Json.fromJson(
                Json.parse(valueApiModel.getData()), DevicePropertyServiceModel.class);
        } catch (Exception e) {
            throw new InvalidInputException("Unable to deserialize deviceProperties from CosmosDB", e);
        }
        TreeSet<String> deviceProperties = new TreeSet<String>();
        for (String tag : devicePropertyServiceModel.getTags()) {
            deviceProperties.add(TAG_PREFIX + tag);
        }
        for (String reported : devicePropertyServiceModel.getReported()) {
            deviceProperties.add(REPORTED_PREFIX + reported);
        }
        TreeSet<String> resultProperties = (TreeSet<String>) deviceProperties.descendingSet();
        return CompletableFuture.supplyAsync(() -> resultProperties);
    }

    /**
     * @summary Update Cache when devices are modified/created
     */
    @Override
    public CompletionStage<DevicePropertyServiceModel> updateListAsync(
        DevicePropertyServiceModel devicePropertyServiceModel)
        throws InterruptedException, ExternalDependencyException {
        if (devicePropertyServiceModel.getReported() == null) {
            devicePropertyServiceModel.setReported(new HashSet<>());
        }
        if (devicePropertyServiceModel.getTags() == null) {
            devicePropertyServiceModel.setTags(new HashSet<>());
        }
        String etag = null;
        while (true) {
            ValueApiModel model = null;
            try {
                model = this.getCurrentDevicePropertiesFromStorage();
            } catch (ExternalDependencyException e) {
                log.error("updateListAsync: External connection unavailable. Retrying after %s seconds",
                    SERVICE_QUERY_INTERVAL_SECS);
                Thread.sleep(this.SERVICE_QUERY_INTERVAL_MS);
            } catch (ResourceNotFoundException e) {
                log.debug("updateListAsync: DeviceProperties doesn't exist in storage.");
            } catch (ConflictingResourceException e) {
                log.debug("updateListAsync: Access to deviceProperties conflicted");
            }
            if (model != null) {
                etag = model.getETag();
                DevicePropertyServiceModel devicePropertiesFromStorage = Json.fromJson(
                    Json.parse(model.getData()), DevicePropertyServiceModel.class);
                this.updateDevicePropertyValues(model, devicePropertyServiceModel);
                // If the new set of deviceProperties are already there in cache, return
                if (devicePropertyServiceModel.getTags().size() == devicePropertiesFromStorage.getTags().size() &&
                    devicePropertyServiceModel.getReported().size() == devicePropertiesFromStorage.getReported().size()) {
                    return CompletableFuture.completedFuture(devicePropertyServiceModel);
                }
            }

            String value = Json.stringify(Json.toJson(devicePropertyServiceModel));
            try {
                return this.storageClient.updateAsync(CacheCollectionId, CacheKey, value, etag).thenApplyAsync(m ->
                    Json.fromJson(Json.parse(m.getData()), DevicePropertyServiceModel.class)
                );
            } catch (ConflictingResourceException e) {
                log.debug("updateListAsync: Access to deviceProperties in storage conflicted with another process.");
                continue;
            } catch (ResourceNotFoundException e) {
                log.debug("updateListAsync: DeviceProperties not found in storage");
                continue;
            }
        }
    }

    /**
     * @summary Try to create cache of deviceProperties if lock failed retry after 10 seconds
     */
    @Override
    public CompletionStage tryRecreateListAsync(boolean force) throws
        InterruptedException, ExternalDependencyException {

        while (true) {
            // Get the current Cache
            ValueApiModel currentCacheValue = new ValueApiModel();
            try {
                currentCacheValue = this.storageClient.getAsync(this.CacheCollectionId, this.CacheKey).toCompletableFuture().get();
            } catch (ConflictingResourceException | ExecutionException e) {
                this.log.warn("tryRecreateListAsync: Get deviceProperties from storage failed. Retrying now.");
                continue;
            } catch (ResourceNotFoundException e) {
                this.log.warn("tryRecreateListAsync: DeviceProperties not found in storage. Creating now.");
            }
            String currentEtag = currentCacheValue.getETag() != null ? currentCacheValue.getETag() : "";

            // Check whether cache needs rebuilding
            if (!shouldCacheRebuild(force, currentCacheValue)) {
                return CompletableFuture.completedFuture(true);
            }

            DevicePropertyServiceModel twinNames = null;
            try {
                twinNames = this.getDevicePropertyNames();
                if (twinNames.isNullOrEmpty()) {
                    this.log.info(String.format("There is no property available to be cached. Retry after %d seconds",
                        this.SERVICE_QUERY_INTERVAL_SECS));
                    Thread.sleep(this.SERVICE_QUERY_INTERVAL_MS);
                    continue;
                }
            } catch (ExternalDependencyException | ExecutionException e) {
                this.log.warn(
                    "Some underlying service is not ready. Retry after %d seconds." + this.SERVICE_QUERY_INTERVAL_SECS);
                Thread.sleep(this.SERVICE_QUERY_INTERVAL_MS);
                continue;
            }

            // Update Storage with new cache
            ValueApiModel updatedCacheValue = new ValueApiModel();
            try {
                updatedCacheValue = this.storageClient.updateAsync(
                    this.CacheCollectionId,
                    this.CacheKey,
                    Json.stringify(Json.toJson(twinNames)),
                    currentEtag).toCompletableFuture().get();
            } catch (ResourceNotFoundException | ExecutionException | ConflictingResourceException e) {
                this.log.warn(
                    "tryRecreateListAsync: Unable to update DeviceProperties in storage. Retry after %d seconds",
                    this.SERVICE_QUERY_INTERVAL_SECS);
                Thread.sleep(this.SERVICE_QUERY_INTERVAL_MS);
                continue;
            }

            if (updatedCacheValue.getETag() != null) {
                return CompletableFuture.completedFuture(true);
            }
        }
    }

    /**
     * @param force         boolean flag to decide if cache needs to be rebuilt.
     * @param valueApiModel An existing valueApiModel to check whether or not cache has expired<
     *
     * @summary A function to decide whether or not cache needs to be rebuilt based on force flag and existing
     * cache's validity
     */
    private boolean shouldCacheRebuild(boolean force, ValueApiModel valueApiModel) {
        if (force) {
            this.log.info("DeviceProperties will be rebuilt due to the force flag");
            return true;
        }

        if (valueApiModel == null) {
            this.log.info("DeviceProperties will be rebuilt since no cache was found");
            return true;
        }

        DevicePropertyServiceModel devicePropertyServiceModel;
        DateTime timestamp;
        try {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");
            timestamp = formatter.parseDateTime(valueApiModel.getMetadata().get("$modified"));
        } catch (Exception e) {
            this.log.info("DeviceProperties will be rebuilt because the last one was broken.");
            return true;
        }
        if (timestamp.plusSeconds(this.TTL).isBeforeNow()) {
            this.log.info("DeviceProperties will be rebuilt because it has expired");
            return true;
        } else {
            this.log.debug("DeviceProperties rebuilding skipped since it has not expired");
            return false;
        }
    }

    /**
     * @return DevicePropertyServiceModel asynchronously
     *
     * @throws ExternalDependencyException
     * @summary Get list of DevicePropertyServiceModel from IOT-hub and whitelist it.
     * @remarks List of Twin Names to be whitelisted is hardcoded in application.conf
     */
    private CompletionStage<DevicePropertyServiceModel> getValidNamesAsync() throws
        ExternalDependencyException, InterruptedException, ExecutionException {
        DevicePropertyServiceModel fullNameWhitelist = new DevicePropertyServiceModel(), prefixWhitelist = new DevicePropertyServiceModel();
        this.parseWhitelist(this.whitelist, fullNameWhitelist, prefixWhitelist);

        DevicePropertyServiceModel validNames = new DevicePropertyServiceModel(
            fullNameWhitelist.getTags(), fullNameWhitelist.getReported());

        if (!prefixWhitelist.getTags().isEmpty() || !prefixWhitelist.getReported().isEmpty()) {
            DevicePropertyServiceModel allNames = devices.getDeviceProperties();
            validNames.getTags().addAll(allNames.getTags().stream().
                filter(m -> prefixWhitelist.getTags().stream().anyMatch(m::startsWith)).collect(Collectors.toSet()));

            validNames.getReported().addAll(allNames.getReported().stream().
                filter(m -> prefixWhitelist.getReported().stream().anyMatch(m::startsWith)).
                collect(Collectors.toSet()));
        }

        return CompletableFuture.supplyAsync(() -> validNames);
    }

    /**
     * @param whitelist         Comma separated list of deviceTwinName to be whitelisted which is hardcoded in
     *                          appsettings.ini.
     * @param fullNameWhitelist An out parameter which is a list of deviceTwinName to be whitelisted without regex.
     * @param prefixWhitelist   An out parameter which is a list of deviceTwinName to be whitelisted with regex.
     *
     * @summary Parse the comma separated string "whitelist" and create two separate list One with regex(*) and one
     * without regex(*)
     */
    private void parseWhitelist(List<String> whitelist,
                                DevicePropertyServiceModel fullNameWhitelist,
                                DevicePropertyServiceModel prefixWhitelist) {

        List<String> tags = whitelist.stream().filter(m -> m.toLowerCase().startsWith(WHITELIST_TAG_PREFIX)).
            map(m -> m.substring(WHITELIST_TAG_PREFIX.length())).collect(Collectors.toList());

        List<String> reported = whitelist.stream().filter(m -> m.toLowerCase().startsWith(WHITELIST_REPORTED_PREFIX)).
            map(m -> m.substring(WHITELIST_REPORTED_PREFIX.length())).collect(Collectors.toList());

        List<String> fixedTags = tags.stream().filter(m -> !m.endsWith("*")).collect(Collectors.toList());
        List<String> fixedReported = reported.stream().filter(m -> !m.endsWith("*")).collect(Collectors.toList());
        List<String> regexTags = tags.stream().filter(m -> m.endsWith("*")).
            map(m -> m.substring(0, m.length() - 1)).collect(Collectors.toList());

        List<String> regexReported = reported.stream().filter(m -> m.endsWith("*")).
            map(m -> m.substring(0, m.length() - 1)).collect(Collectors.toList());

        fullNameWhitelist.setTags(new HashSet<>(fixedTags));
        fullNameWhitelist.setReported(new HashSet<>(fixedReported));
        prefixWhitelist.setTags(new HashSet<>(regexTags));
        prefixWhitelist.setReported(new HashSet<>(regexReported));
    }

    /**
     * @summary Get current device properties saved in cosmosDB cache
     */
    private ValueApiModel getCurrentDevicePropertiesFromStorage() throws
        ExternalDependencyException, ResourceNotFoundException, ConflictingResourceException {
        try {
            return this.storageClient.getAsync(CacheCollectionId, CacheKey).toCompletableFuture().get();
        } catch (ExecutionException | InterruptedException e) {
            throw new ExternalDependencyException("Unable to get deviceProperties from storage", e);
        }
    }

    /**
     * @summary Get List of deviceProperties from cache
     */
    private void updateDevicePropertyValues(ValueApiModel valueApiModel, DevicePropertyServiceModel devicePropertiesToAdd) {
        if (valueApiModel != null) {
            DevicePropertyServiceModel devicePropertiesFromStorage;
            try {
                devicePropertiesFromStorage = Json.fromJson(
                    Json.parse(valueApiModel.getData()), DevicePropertyServiceModel.class);
            } catch (Exception e) {
                devicePropertiesFromStorage = new DevicePropertyServiceModel();
            }
            if (devicePropertiesFromStorage.getTags() == null) {
                devicePropertiesFromStorage.setTags(new HashSet<String>());
            }
            if (devicePropertiesFromStorage.getReported() == null) {
                devicePropertiesFromStorage.setReported(new HashSet<String>());
            }
            devicePropertiesToAdd.getTags().addAll(devicePropertiesFromStorage.getTags());
            devicePropertiesToAdd.getReported().addAll(devicePropertiesFromStorage.getReported());
        }
    }

    /**
     * @summary Get List of deviceProperties from cache
     */
    private DevicePropertyServiceModel getDevicePropertyNames()
        throws ExternalDependencyException, ExecutionException, InterruptedException {
        CompletableFuture<DevicePropertyServiceModel> twinNamesTask = this.getValidNamesAsync().toCompletableFuture();
        DevicePropertyServiceModel twinNames = twinNamesTask.get();
        return twinNames;
    }
}
