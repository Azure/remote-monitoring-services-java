// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.RecurringTasksAgent;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.microsoft.azure.iotsolutions.iothubmanager.services.IDeviceProperties;
import play.Logger;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Singleton
public class Agent implements IRecurringTasksAgent {

    // When DeviceProperties cache initialization fails, retry in few seconds
    private static final int CACHE_INIT_RETRY_SECS = 10;

    // After the DeviceProperties cache is initialized, update it every few minutes
    private static final int CACHE_UPDATE_SECS = 300;

    // When generating the DeviceProperties cache, allow some time to finish, at least one minute
    private static final int CACHE_TIMEOUT_SECS = 90;

    private final IDeviceProperties cache;
    private static final Logger.ALogger log = Logger.of(Agent.class);

    @Inject
    public Agent(IDeviceProperties cache) {
        this.cache = cache;
        CompletableFuture.runAsync(() -> this.run());
    }

    @Override
    public void run() {
        this.buildDevicePropertiesCache();
        this.scheduleDevicePropertiesCacheUpdate();
    }

    private void buildDevicePropertiesCache() {
        while (true) {
            try {
                this.log.info("Creating DeviceProperties cache...");
                this.cache.tryRecreateListAsync().toCompletableFuture().get(CACHE_TIMEOUT_SECS * 1000, TimeUnit.SECONDS);
                this.log.info("DeviceProperties cache created");
                return;
            } catch (Exception e) {
                this.log.debug("DeviceProperties creation failed, will retry in few seconds");
            }

            this.log.warn("Pausing thread before retrying cache creation");
            try {
                Thread.sleep(CACHE_INIT_RETRY_SECS * 1000);
            } catch (InterruptedException e) {
                this.log.error(String.format("buildDevicePropertiesCache sleep failed :%d seconds", CACHE_INIT_RETRY_SECS));
            }
        }
    }

    private void scheduleDevicePropertiesCacheUpdate() {
        try {
            this.log.info("Scheduling a DeviceProperties cache update");
            Timer timer = new Timer("DeviceProperties cache update", true);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    updateDevicePropertiesCache();
                }
            }, 1000 * CACHE_UPDATE_SECS);
            this.log.info("DeviceProperties cache update scheduled");
        } catch (Exception e) {
            this.log.error("DeviceProperties cache update scheduling failed", e);
        }
    }

    private void updateDevicePropertiesCache() {
        try {
            this.log.info("Updating DeviceProperties cache...");
            this.cache.tryRecreateListAsync().toCompletableFuture().get(CACHE_TIMEOUT_SECS * 1000, TimeUnit.SECONDS);
            this.log.info("DeviceProperties cache updated");
        } catch (Exception e) {
            this.log.warn("DeviceProperties cache update failed, will retry later", e);
        }
        this.scheduleDevicePropertiesCacheUpdate();
    }
}
