// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.BaseException;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.ConflictingResourceException;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.ResourceNotFoundException;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.IIothubManagerServiceClient;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.ISimulationServiceClient;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.IStorageAdapterClient;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.ValueApiModel;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.CacheValue;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.DeviceTwinName;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.IServicesConfig;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeParser;
import play.Logger;
import play.libs.Json;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

@Singleton
public class Cache implements ICache {

    private final IStorageAdapterClient storageClient;
    private final IIothubManagerServiceClient iotHubClient;
    private final ISimulationServiceClient simulationClient;
    private static final Logger.ALogger log = Logger.of(Cache.class);
    private final int cacheTTL;
    private final int rebuildTimeout;
    private final String CacheCollectionId = "cache";
    private final String CacheKey = "twin";

    @Inject
    public Cache(IStorageAdapterClient storageClient,
                 IIothubManagerServiceClient iotHubClient,
                 ISimulationServiceClient simulationClient,
                 IServicesConfig config) throws ExternalDependencyException {
        this.storageClient = storageClient;
        this.iotHubClient = iotHubClient;
        this.simulationClient = simulationClient;
        this.cacheTTL = config.getCacheTTL();
        this.rebuildTimeout = config.getCacheRebuildTimeout();
        // global setting is not recommend for application_onStart event, PLS refer here for details :https://www.playframework.com/documentation/2.6.x/GlobalSettings
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                    RebuildCacheAsync().toCompletableFuture().get();
                } catch (Exception e) {
                    Logger.of(Seed.class).error("RebuildCacheAsync");
                }
            }
        }).start();
    }

    @Override
    public CompletionStage<CacheValue> GetCacheAsync() {
        try {
            return storageClient.getAsync(CacheCollectionId, CacheKey).thenApplyAsync(m ->
                    Json.fromJson(Json.parse(m.getData()), CacheValue.class)
            );
        } catch (Exception ex) {
            log.info(String.format("%s:%s not found.", CacheCollectionId, CacheKey));
            return CompletableFuture.supplyAsync(() -> new CacheValue(new HashSet<String>(), new HashSet<String>()));
        }
    }

    @Override
    public CompletionStage<CacheValue> SetCacheAsync(CacheValue cache) throws BaseException {
        if (cache.getReported() == null) {
            cache.setReported(new HashSet<String>());
        }
        if (cache.getTags() == null) {
            cache.setTags(new HashSet<String>());
        }
        String etag = null;
        while (true) {
            ValueApiModel model = null;
            try {
                model = this.storageClient.getAsync(CacheCollectionId, CacheKey).toCompletableFuture().get();
            } catch (ResourceNotFoundException e) {
                log.info(String.format("SetCacheAsync %s:%s not found.", CacheCollectionId, CacheKey));
            } catch (InterruptedException | ExecutionException e) {
                log.error(String.format("SetCacheAsync InterruptedException occured in storageClient.getAsync(%s, %s).", CacheCollectionId, CacheKey));
                throw new ExternalDependencyException("SetCacheAsync failed");
            }
            if (model != null) {
                CacheValue cacheServer;
                try {
                    cacheServer = Json.fromJson(Json.parse(model.getData()), CacheValue.class);
                } catch (Exception e) {
                    cacheServer = new CacheValue();
                }
                if (cacheServer.getTags() == null) {
                    cacheServer.setTags(new HashSet<String>());
                }
                if (cacheServer.getReported() == null) {
                    cacheServer.setReported(new HashSet<String>());
                }
                cache.getTags().addAll(cacheServer.getTags());
                cache.getReported().addAll(cacheServer.getReported());
                etag = model.getETag();
                if (cache.getTags().size() == cacheServer.getTags().size() && cache.getReported().size() == cacheServer.getReported().size()) {
                    return CompletableFuture.supplyAsync(() -> cache);
                }
            }

            String value = Json.stringify(Json.toJson(cache));
            try {
                return this.storageClient.updateAsync(CacheCollectionId, CacheKey, value, etag).thenApplyAsync(m ->
                        Json.fromJson(Json.parse(m.getData()), CacheValue.class)
                );
            } catch (ConflictingResourceException e) {
                log.info("SetCacheAsync Conflicted ");
                continue;
            }
        }
    }

    @Override
    public CompletionStage RebuildCacheAsync(boolean force) throws BaseException, ExecutionException, InterruptedException, URISyntaxException {
        {
            int retry = 5;
            while (true) {
                HashSet<String> reportedNames = null;
                DeviceTwinName twinNames = null;
                ValueApiModel cache = null;
                String etag = null;

                try {
                    cache = this.storageClient.getAsync(CacheCollectionId, CacheKey).toCompletableFuture().get();
                } catch (Exception e) {
                    log.info(String.format("RebuildCacheAsync:%s:%s not found.", CacheCollectionId, CacheKey));
                }

                boolean needBuild = this.NeedBuild(force, cache);
                if (!needBuild) {
                    return CompletableFuture.runAsync(() -> {
                    });
                }

                try {
                    CompletableFuture<DeviceTwinName> twinNamesTask = this.iotHubClient.GetDeviceTwinNamesAsync().toCompletableFuture();
                    CompletableFuture<HashSet<String>> reportedNamesTask = this.simulationClient.GetDevicePropertyNamesAsync().toCompletableFuture();
                    CompletableFuture.allOf(twinNamesTask, reportedNamesTask).get();
                    twinNames = twinNamesTask.get();
                    reportedNames = reportedNamesTask.get();
                    reportedNames.addAll(twinNames.getReportedProperties());
                } catch (Exception e) {
                    log.info(String.format("retry %d for %s:%s  IothubManagerService and SimulationService  are not both ready,wait 10 seconds", retry, CacheCollectionId, CacheKey));
                    if (retry-- < 1) {
                        return CompletableFuture.runAsync(() -> {
                        });
                    }
                    Thread.sleep(10000);
                    continue;
                }

                if (cache != null) {
                    CacheValue model = Json.fromJson(Json.parse(cache.getData()), CacheValue.class);
                    model.setRebuilding(true);
                    ValueApiModel response = this.storageClient.updateAsync(CacheCollectionId, CacheKey, Json.stringify(Json.toJson(model)),
                            cache.getETag()).
                            toCompletableFuture().get();
                    etag = response.getETag();
                } else {
                    ValueApiModel response = this.storageClient.updateAsync(CacheCollectionId, CacheKey,
                            Json.stringify(Json.toJson(new CacheValue(null, null, false))), null).
                            toCompletableFuture().get();
                    etag = response.getETag();
                }
                String value = Json.stringify(Json.toJson(new CacheValue(twinNames.getTags(), reportedNames, false)));
                try {
                    return this.storageClient.updateAsync(CacheCollectionId, CacheKey, value, etag).thenAcceptAsync(m -> {
                    });
                } catch (ConflictingResourceException e) {
                    log.info("rebuild Conflicted ");
                    continue;
                }
            }
        }
    }

    private boolean NeedBuild(boolean force, ValueApiModel twin) {
        boolean needBuild = false;
        // validate timestamp
        if (force || twin == null) {
            needBuild = true;
        } else {
            boolean rebuilding = Json.fromJson(Json.parse(twin.getData()), CacheValue.class).isRebuilding();
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");
            DateTime timestamp = formatter.parseDateTime(twin.getMetadata().get("$modified"));
            needBuild = needBuild || !rebuilding && timestamp.plusSeconds(this.cacheTTL).isBeforeNow();
            needBuild = needBuild || rebuilding && timestamp.plusSeconds(this.rebuildTimeout).isBeforeNow();
        }
        return needBuild;
    }
}
