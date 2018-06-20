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

    private final ISeed seed;
    private static final Logger.ALogger log = Logger.of(RecurringTasks.class);

    @Inject
    public RecurringTasks(
            ISeed seed) {
        this.seed = seed;
        CompletableFuture.runAsync(() -> this.run());
    }

    @Override
    public void run() {
        this.setupSeedData();
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
}
