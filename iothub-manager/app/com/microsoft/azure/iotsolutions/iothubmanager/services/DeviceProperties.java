// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.microsoft.azure.iotsolutions.iothubmanager.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.*;
import com.microsoft.azure.iotsolutions.iothubmanager.services.external.*;
import com.microsoft.azure.iotsolutions.iothubmanager.services.helpers.StorageWriteLock;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DevicePropertyServiceModel;
import com.microsoft.azure.iotsolutions.iothubmanager.services.models.DeviceTwinName;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import play.Logger;
import play.libs.Json;

import java.net.URISyntaxException;
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
    // Hardcoded in application.conf
    private final int rebuildTimeout;
    private final String CacheCollectionId = "device-twin-properties";
    private final String CacheKey = "cache";
    // Hardcoded in application.conf
    private final List<String> whitelist;
    private static final String WHITELIST_TAG_PREFIX = "tags.";
    private static final String WHITELIST_REPORTED_PREFIX = "reported.";
    private static final long SERVICE_QUERY_INTERVAL_SECS = 10;
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
        this.rebuildTimeout = config.getDevicePropertiesRebuildTimeout();
        this.whitelist = config.getDevicePropertiesWhiteList();
        this.devices = devices;
    }

    /**
     * @summary Get List of deviceProperties from cache
     */
    @Override
    public CompletionStage<TreeSet<String>> getListAsync() {
        try {
            ValueApiModel valueApiModel = storageClient.getAsync(CacheCollectionId, CacheKey).
                toCompletableFuture().get();
            DevicePropertyServiceModel devicePropertyServiceModel = Json.fromJson(
                Json.parse(valueApiModel.getData()), DevicePropertyServiceModel.class);
            TreeSet<String> deviceProperties = new TreeSet<String>();
            for (String tag : devicePropertyServiceModel.getTags()) {
                deviceProperties.add(TAG_PREFIX + tag);
            }
            for (String reported : devicePropertyServiceModel.getReported()) {
                deviceProperties.add(REPORTED_PREFIX + reported);
            }
            TreeSet<String> resultProperties = (TreeSet<String>) deviceProperties.descendingSet();
            return CompletableFuture.supplyAsync(() -> resultProperties);
        } catch (Exception ex) {
            log.debug(String.format("%s:%s not found.", CacheCollectionId, CacheKey), ex);
            return CompletableFuture.supplyAsync(() -> new TreeSet<String>());
        }
    }

    /**
     * @summary Update Cache when devices are modified/created
     */
    @Override
    public CompletionStage<DevicePropertyServiceModel> updateListAsync(
        DevicePropertyServiceModel devicePropertyServiceModel) throws BaseException {
        if (devicePropertyServiceModel.getReported() == null) {
            devicePropertyServiceModel.setReported(new HashSet<>());
        }
        if (devicePropertyServiceModel.getTags() == null) {
            devicePropertyServiceModel.setTags(new HashSet<>());
        }
        String etag = null;
        while (true) {
            ValueApiModel model = this.getCurrentDevicePropertiesFromStorage();
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
                log.info("updateListAsync: Access to deviceProperties in CosmosDB conflicted with another process.");
                continue;
            } catch (Exception e) {
                log.error("updateListAsync : Could not update deviceProperties in CosmosDB.", e.getMessage());
                throw new CompletionException(e);
            }
        }
    }

    /**
     * @summary Try to create cache of deviceProperties if lock failed retry after 10 seconds
     */
    @Override
    public CompletionStage tryRecreateListAsync(boolean force) throws Exception {
        StorageWriteLock<DevicePropertyServiceModel> lock = new StorageWriteLock<>(
            DevicePropertyServiceModel.class,
            this.storageClient,
            CacheCollectionId,
            CacheKey,
            (c, b) -> c.setRebuilding(b),
            m -> this.shouldCacheRebuild(force, m));

        while (true) {
            // Try to read non-empty twin data at first before locking cache entry
            // to improve lock condition in case lock has been acquired but twin data
            // might be still unavailable. When cache data is available, it will be
            // safer to write an empty cache data in order to acquire lock and then
            // update twin data into the cache entry.
            DeviceTwinName twinNames = null;
            try {
                twinNames = this.getDevicePropertyNames();
                if (twinNames.isEmpty()) {
                    this.log.info(String.format("There is no property available to be cached. Retry after %d seconds",
                        this.SERVICE_QUERY_INTERVAL_SECS));
                    Thread.sleep(this.SERVICE_QUERY_INTERVAL_SECS * 1000);
                    continue;
                }
            } catch (Exception e) {
                this.log.warn("Some underlying service is not ready. Retry after " + this.SERVICE_QUERY_INTERVAL_SECS);
                Thread.sleep(this.SERVICE_QUERY_INTERVAL_SECS * 1000);
                continue;
            }

            Optional<Boolean> locked = this.lockCache(lock);
            if (locked == null) {
                this.log.warn(
                    String.format("DeviceProperties rebuilding: lock failed due to conflict. Retry after %d seconds",
                        this.SERVICE_QUERY_INTERVAL_SECS));
                Thread.sleep(this.SERVICE_QUERY_INTERVAL_SECS * 1000);
                continue;
            }
            if (!locked.get()) {
                return CompletableFuture.completedFuture(false);
            }
            Boolean updated = this.writeAndUnlockCache(lock, twinNames);

            if (updated) {
                return CompletableFuture.completedFuture(true);
            }

            this.log.warn("The cache failed to be written due to conflict. Retry soon");
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
            devicePropertyServiceModel = Json.fromJson(
                Json.parse(valueApiModel.getData()), DevicePropertyServiceModel.class);
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");
            timestamp = formatter.parseDateTime(valueApiModel.getMetadata().get("$modified"));
        } catch (Exception e) {
            this.log.info("DeviceProperties will be rebuilt because the last one was broken.");
            return true;
        }

        if (devicePropertyServiceModel.isRebuilding()) {
            if (timestamp.plusSeconds(this.rebuildTimeout).isBeforeNow()) {
                this.log.debug("DeviceProperties will be rebuilt because last rebuilding timed-out");
                return true;
            }
            this.log.debug("DeviceProperties rebuilding skipped because it is being rebuilt by other instance");
            return false;
        } else if (devicePropertyServiceModel.isNullOrEmpty()) {
            this.log.info("DeviceProperties will be rebuilt because it is empty");
            return true;
        } else if (timestamp.plusSeconds(this.TTL).isBeforeNow()) {
            this.log.info("DeviceProperties will be rebuilt because it has expired");
            return true;
        } else {
            this.log.debug("DeviceProperties rebuilding skipped since it has not expired");
            return false;
        }
    }

    /**
     * @return DeviceTwinName asynchronously
     *
     * @throws ExternalDependencyException
     * @summary Get list of DeviceTwinNames from IOT-hub and whitelist it.
     * @remarks List of Twin Names to be whitelisted is hardcoded in application.conf
     */
    private CompletionStage<DeviceTwinName> getValidNamesAsync() throws ExternalDependencyException {
        DeviceTwinName fullNameWhitelist = new DeviceTwinName(), prefixWhitelist = new DeviceTwinName();
        this.parseWhitelist(this.whitelist, fullNameWhitelist, prefixWhitelist);

        DeviceTwinName validNames = new DeviceTwinName(
            fullNameWhitelist.getTags(),
            fullNameWhitelist.getReportedProperties());

        if (!prefixWhitelist.getTags().isEmpty() || !prefixWhitelist.getReportedProperties().isEmpty()) {
            DeviceTwinName allNames = devices.getDeviceTwinNames();
            validNames.getTags().addAll(allNames.getTags().stream().
                filter(m -> prefixWhitelist.getTags().stream().anyMatch(m::startsWith)).collect(Collectors.toSet()));

            validNames.getReportedProperties().addAll(allNames.getReportedProperties().stream().
                filter(m -> prefixWhitelist.getReportedProperties().stream().anyMatch(m::startsWith)).
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
                                DeviceTwinName fullNameWhitelist,
                                DeviceTwinName prefixWhitelist) {

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
        fullNameWhitelist.setReportedProperties(new HashSet<>(fixedReported));
        prefixWhitelist.setTags(new HashSet<>(regexTags));
        prefixWhitelist.setReportedProperties(new HashSet<>(regexReported));
    }

    /**
     * @summary Get current device properties saved in cosmosDB cache
     */
    private ValueApiModel getCurrentDevicePropertiesFromStorage() throws ExternalDependencyException {
        try {
            return this.storageClient.getAsync(CacheCollectionId, CacheKey).toCompletableFuture().get();
        } catch (ResourceNotFoundException e) {
            log.info(String.format("updateListAsync %s:%s not found.", CacheCollectionId, CacheKey));
        } catch (InterruptedException | ExecutionException | BaseException e) {
            log.error(String.format("updateListAsync InterruptedException occurred in storageClient.getAsync(%s, %s).",
                CacheCollectionId, CacheKey));
            throw new ExternalDependencyException("updateListAsync failed");
        }
        return null;
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
     * @param lock of type StorageWriteLock<DevicePropertyServiceModel>
     *
     * @summary Lock cache in cosmosDB to avoid conflicts
     */
    private Optional<Boolean> lockCache(StorageWriteLock<DevicePropertyServiceModel> lock)
        throws ExternalDependencyException, ResourceOutOfDateException {
        try {
            return lock.tryLockAsync().toCompletableFuture().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new ExternalDependencyException("failed to lock");
        }
    }

    /**
     * @summary Get List of deviceProperties from cache
     */
    private DeviceTwinName getDevicePropertyNames()
        throws ExternalDependencyException, URISyntaxException, ExecutionException, InterruptedException {
        CompletableFuture<DeviceTwinName> twinNamesTask = this.getValidNamesAsync().toCompletableFuture();
        DeviceTwinName twinNames = twinNamesTask.get();
        return twinNames;
    }

    /**
     * @param lock                  of type StorageWriteLock<DevicePropertyServiceModel>
     * @param twinPropertiesAndTags device twin properties and tags
     *
     * @summary Write device twin properties and tags to cache in cosmosDB and release lock
     */
    private Boolean writeAndUnlockCache(StorageWriteLock<DevicePropertyServiceModel> lock, DeviceTwinName twinPropertiesAndTags)
        throws ExternalDependencyException, ResourceOutOfDateException {
        try {
            return lock.writeAndReleaseAsync(
                new DevicePropertyServiceModel(
                    twinPropertiesAndTags.getTags(),
                    twinPropertiesAndTags.getReportedProperties())).toCompletableFuture().get();
        } catch (InterruptedException | ExecutionException e) {
            String errorMessage = String.format("failed to WriteAndRelease lock for %s,%s ", CacheCollectionId, CacheKey);
            this.log.error(errorMessage, e);
            throw new ExternalDependencyException(errorMessage);
        }
    }
}
