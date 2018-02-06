// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import play.Logger;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Singleton
public class RecurringTasks implements IRecurringTasks {

    // When seed data creation fails, retry in few seconds
    // using a simple backoff logic
    private static final int SEED_RETRY_INIT_SECS = 1;
    private static final int SEED_RETRY_MAX_SECS = 8;

    // Allow some time for seed data to be created, shouldn't take too long though
    private static final int SEED_TIMEOUT_SECS = 30;

    // When cache initialization fails, retry in few seconds
    private static final int CACHE_INIT_RETRY_SECS = 10;

    // After the cache is initialized, update it every few minutes
    private static final int CACHE_UPDATE_SECS = 300;

    // When generating the cache, allow some time to finish, at least one minute
    private static final int CACHE_TIMEOUT_SECS = 90;

    private final ISeed seed;
    private final ICache cache;
    private static final Logger.ALogger log = Logger.of(RecurringTasks.class);

    @Inject
    public RecurringTasks(
            ISeed seed,
            ICache cache) {
        this.seed = seed;
        this.cache = cache;
        CompletableFuture.runAsync(() -> this.run());
    }

    @Override
    public void run() {
        this.setupSeedData();
        this.buildCache();
        this.scheduleCacheUpdate();
    }

    private void setupSeedData() {
        int pauseSecs = SEED_RETRY_INIT_SECS;
        while (true) {
            try {
                this.log.info("Creating seed data...");
                this.seed.trySeedAsync().toCompletableFuture().get(SEED_TIMEOUT_SECS, TimeUnit.SECONDS);
                this.log.info("Seed data created");
                return;
            } catch (Exception e) {
                this.log.warn("Seed data setup failed, will retry in few seconds");
            }

            this.log.warn("Pausing thread before retrying seed data");
            try {
                Thread.sleep(pauseSecs * 1000);
            } catch (InterruptedException e) {
                this.log.error(String.format("Seed data sleep failed :%d seconds", pauseSecs));
            }

            // Increase the pause, up to a maximum
            pauseSecs = Math.min(pauseSecs + 1, SEED_RETRY_MAX_SECS);
        }
    }

    private void buildCache() {
        while (true) {
            try {
                this.log.info("Creating cache...");
                this.cache.rebuildCacheAsync().toCompletableFuture().get(CACHE_TIMEOUT_SECS * 1000, TimeUnit.SECONDS);
                this.log.info("Cache created");
                return;
            } catch (Exception e) {
                this.log.warn("Cache creation failed, will retry in few seconds", e);
            }

            this.log.warn("Pausing thread before retrying cache creation");
            try {
                Thread.sleep(CACHE_INIT_RETRY_SECS * 1000);
            } catch (InterruptedException e) {
                this.log.error(String.format("BuildCache sleep failed :%d seconds", CACHE_INIT_RETRY_SECS));
            }
        }
    }

    private void scheduleCacheUpdate() {
        try {
            this.log.info("Scheduling a cache update");
            Timer timer = new Timer("cache update", true);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    updateCache();
                }
            }, 1000 * CACHE_UPDATE_SECS);
            this.log.info("Cache update scheduled");
        } catch (Exception e) {
            this.log.error("Cache update scheduling failed", e);
        }
    }

    private void updateCache() {
        try {
            this.log.info("Updating cache...");
            this.cache.rebuildCacheAsync().toCompletableFuture().get(CACHE_TIMEOUT_SECS * 1000, TimeUnit.SECONDS);
            this.log.info("Cache updated");
        } catch (Exception e) {
            this.log.warn("Cache update failed, will retry later");
        }
        this.scheduleCacheUpdate();
    }
}
