// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import play.Logger;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Singleton
public class RecurringTasks implements IRecurringTasks {

    // When cache initialization fails, retry in few seconds
    private static final int CACHE_INIT_RETRY_SECS = 10;

    // After the cache is initialized, update it every few minutes
    private static final int CACHE_UPDATE_SECS = 300;

    // When generating the cache, allow some time to finish, at least one minute
    private static final int CACHE_TIMEOUT_SECS = 90;

    private final IDeviceProperties cache;
    private static final Logger.ALogger log = Logger.of(RecurringTasks.class);

    @Inject
    public RecurringTasks(IDeviceProperties cache) {
        this.cache = cache;
        CompletableFuture.runAsync(() -> this.run());
    }

    @Override
    public void run() {
        this.buildCache();
        this.scheduleCacheUpdate();
    }

    private void buildCache() {
        while (true) {
            try {
                this.log.info("Creating cache...");
                this.cache.TryRecreateListAsync().toCompletableFuture().get(CACHE_TIMEOUT_SECS * 1000, TimeUnit.SECONDS);
                this.log.info("DeviceProperties created");
                return;
            } catch (Exception e) {
                this.log.warn("DeviceProperties creation failed, will retry in few seconds", e);
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
            this.log.info("DeviceProperties update scheduled");
        } catch (Exception e) {
            this.log.error("DeviceProperties update scheduling failed", e);
        }
    }

    private void updateCache() {
        try {
            this.log.info("Updating cache...");
            this.cache.TryRecreateListAsync().toCompletableFuture().get(CACHE_TIMEOUT_SECS * 1000, TimeUnit.SECONDS);
            this.log.info("DeviceProperties updated");
        } catch (Exception e) {
            this.log.warn("DeviceProperties update failed, will retry later", e);
        }
        this.scheduleCacheUpdate();
    }
}
