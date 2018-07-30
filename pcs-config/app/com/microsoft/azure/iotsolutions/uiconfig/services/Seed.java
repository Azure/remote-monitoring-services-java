// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.BaseException;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.ExternalDependencyException;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.ResourceNotFoundException;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.*;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.DeviceGroup;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.Template;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.IServicesConfig;
import play.Logger;
import play.libs.Json;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Singleton
public class Seed implements ISeed {

    private final String SeedCollectionId = "solution-settings";
    private final String MutexKey = "seedMutex";
    private final String CompletedFlagKey = "seedCompleted";
    private int mutexTimeout = 60 * 5;// seconds

    private IServicesConfig config;
    private IStorageMutex mutex;
    private IStorage storage;
    private IStorageAdapterClient storageClient;
    private IDeviceSimulationClient simulationClient;
    private IDeviceTelemetryClient telemetryClient;
    private static final Logger.ALogger log = Logger.of(Seed.class);

    @Inject
    public Seed(
            IServicesConfig config,
            IStorageMutex mutex,
            IStorage storage,
            IStorageAdapterClient storageClient,
            IDeviceSimulationClient simulationClient,
            IDeviceTelemetryClient telemetryClient) throws ExternalDependencyException {
        this.config = config;
        this.mutex = mutex;
        this.storage = storage;
        this.storageClient = storageClient;
        this.simulationClient = simulationClient;
        this.telemetryClient = telemetryClient;
    }

    @Override
    public CompletionStage trySeedAsync() throws ExternalDependencyException {
        try {
            if (!(this.mutex.enterAsync(SeedCollectionId, MutexKey, this.mutexTimeout).toCompletableFuture().get().booleanValue())) {
                this.log.info("Seed skipped (conflict)");
                return CompletableFuture.completedFuture(Optional.empty());
            }
        } catch (InterruptedException | ExecutionException | BaseException e) {
            log.error("mutex.EnterAsync failed");
            throw new ExternalDependencyException("Seed failed");
        }
        try {
            if (this.checkCompletedFlagAsync().toCompletableFuture().get().booleanValue()) {
                this.log.info("Seed skipped (completed)");
                return CompletableFuture.completedFuture(Optional.empty());
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("CheckCompletedFlagAsync failed");
            throw new ExternalDependencyException("Seed failed");
        }

        try {
            this.log.info("Seed begin");
            this.seedAsync(this.config.getSeedTemplate());
            this.log.info("Seed end");
            this.setCompletedFlagAsync().toCompletableFuture().get();
            this.mutex.leaveAsync(SeedCollectionId, MutexKey).toCompletableFuture().get();
            return CompletableFuture.completedFuture(Optional.empty());
        } catch (Exception e) {
            log.error("Seed failed", e);
            throw new ExternalDependencyException("Seed failed", e);
        }
    }

    private CompletionStage<Boolean> checkCompletedFlagAsync() throws ExternalDependencyException {
        try {
            return this.storageClient.getAsync(SeedCollectionId, CompletedFlagKey).thenApplyAsync(m -> new Boolean(true));
        } catch (ResourceNotFoundException e) {
            log.error(String.format("%s,%s is not found", SeedCollectionId, CompletedFlagKey));
            return CompletableFuture.completedFuture(new Boolean(false));
        } catch (BaseException e) {
            throw new ExternalDependencyException("CheckCompletedFlagAsync failed");
        }
    }

    private CompletionStage setCompletedFlagAsync() throws ExternalDependencyException {
        try {
            return this.storageClient.updateAsync(SeedCollectionId, CompletedFlagKey, "true", "*");
        } catch (BaseException e) {
            log.error(String.format("%s,%s SetCompletedFlagAsync failed", SeedCollectionId, CompletedFlagKey));
            throw new ExternalDependencyException("SetCompletedFlagAsync failed");
        }
    }

    private CompletionStage seedAsync(String template)
        throws ExternalDependencyException, InvalidInputException, ResourceNotFoundException {
        String templatePath = String.format("/resources/data/%s.json", template);
        String content = "";
        try (InputStream is = this.getClass().getResourceAsStream(templatePath)) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line = reader.readLine();
                while (line != null) {
                    content += line;
                    line = reader.readLine();
                }
            }
        } catch (Exception e) {
            String message = String.format("Seed template %s.json does not exist", template);
            log.error(message, e);
            throw new ResourceNotFoundException(message, e);
        }
        return this.seedSingleTemplateAsync(content);
    }

    private CompletionStage seedSingleTemplateAsync(String content) throws InvalidInputException, ExternalDependencyException {
        Template template;
        try {
            template = Json.fromJson(Json.parse(content), Template.class);
        } catch (Exception ex) {
            throw new InvalidInputException("Failed to parse template", ex);
        }

        if (StreamSupport.stream(template.getGroups().spliterator(), false).
                map(m -> m.getId()).distinct().count() != Iterables.size(template.getGroups())) {
            this.log.warn("Found duplicated group ID");
        }

        if (StreamSupport.stream(template.getRules().spliterator(), false).map(m -> m.getId()).distinct().count() !=
                Iterables.size(template.getRules())) {
            this.log.warn("Found duplicated rule ID");
        }

        HashSet<String> groupIds = new HashSet<String>(StreamSupport.stream(template.getGroups().spliterator(), false).
                map(m -> m.getId()).collect(Collectors.toSet()));
        List<RuleApiModel> rulesWithInvalidGroupId = StreamSupport.stream(template.getRules().spliterator(), false).
                filter(m -> !groupIds.contains(m.getGroupId())).collect(Collectors.toList());
        if (rulesWithInvalidGroupId.size() > 0) {
            this.log.warn("Invalid group ID found in rules");
        }
        for (DeviceGroup group : template.getGroups()) {
            try {
                this.storage.updateDeviceGroupAsync(group.getId(), group, "*");
            } catch (Exception e) {
                String errorMessage = String.format("Failed to seed default group %s", group.getDisplayName());
                this.log.error(errorMessage, e);
                throw new ExternalDependencyException(errorMessage, e);
            }
        }

        for (RuleApiModel rule : template.getRules()) {
            try {
                this.telemetryClient.updateRuleAsync(rule, "*");
            } catch (Exception e) {
                String errorMessage = String.format("Failed to seed default rule %s", rule.getDescription());
                this.log.error(errorMessage, e);
                throw new ExternalDependencyException(errorMessage, e);
            }
        }

        try {
            SimulationApiModel simulationModel = this.simulationClient.getSimulationAsync().toCompletableFuture().get();
            if (simulationModel != null) {
                this.log.info("Skip seed simulation since there is already one simuation");
            } else {
                simulationModel = new SimulationApiModel(Lists.newArrayList(template.getDeviceModels()), "*", "1");
                this.simulationClient.updateSimulationAsync(simulationModel).toCompletableFuture().get();
            }
        } catch (Exception e) {
            String errorMessage = "Failed to seed default simulation";
            this.log.error(errorMessage, e);
            throw new ExternalDependencyException(errorMessage, e);
        }
        return CompletableFuture.completedFuture(Optional.empty());
    }
}
