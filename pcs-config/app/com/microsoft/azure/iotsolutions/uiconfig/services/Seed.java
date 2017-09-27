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
import com.microsoft.azure.iotsolutions.uiconfig.services.models.Template;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.IServicesConfig;
import play.Logger;
import play.libs.Json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
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
    private int mutexTimeout = 60 * 5;

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

        // global setting is not recommend for application_onStart event, PLS refer here for details :https://www.playframework.com/documentation/2.6.x/GlobalSettings
        try {
            TrySeedAsync().toCompletableFuture().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new ExternalDependencyException("Seed failed");
        }
    }

    @Override
    public CompletionStage TrySeedAsync() throws ExternalDependencyException {
        try {
            if (!(this.mutex.EnterAsync(SeedCollectionId, MutexKey, this.mutexTimeout).toCompletableFuture().get().booleanValue())) {
                this.log.info("Seed skipped (conflict)");
                return CompletableFuture.runAsync(() -> {
                });
            }
        } catch (InterruptedException | ExecutionException | BaseException e) {
            log.error("mutex.EnterAsync failed");
            throw new ExternalDependencyException("Seed failed");
        }
        try {
            if (this.CheckCompletedFlagAsync().toCompletableFuture().get().booleanValue()) {
                this.log.info("Seed skipped (completed)");
                return CompletableFuture.runAsync(() -> {
                });
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("CheckCompletedFlagAsync failed");
            throw new ExternalDependencyException("Seed failed");
        }

        try {
            this.log.info("Seed begin");
            this.SeedAsync(this.config.getSeedTemplate());
            this.log.info("Seed end");
            this.SetCompletedFlagAsync().toCompletableFuture().get();
            this.mutex.LeaveAsync(SeedCollectionId, MutexKey).toCompletableFuture().get();
        } catch (InterruptedException | ExecutionException | BaseException e) {
            log.error("Seed failed");
            throw new ExternalDependencyException("Seed failed");
        }
        return CompletableFuture.runAsync(() -> {
        });
    }

    private CompletionStage<Boolean> CheckCompletedFlagAsync() throws ExternalDependencyException {
        try {
            return this.storageClient.getAsync(SeedCollectionId, CompletedFlagKey).thenApplyAsync(m -> new Boolean(true));
        } catch (ResourceNotFoundException e) {
            log.error(String.format("%s,%s is not found", SeedCollectionId, CompletedFlagKey));
            return CompletableFuture.supplyAsync(() -> new Boolean(false));
        } catch (BaseException e) {
            throw new ExternalDependencyException("CheckCompletedFlagAsync failed");
        }
    }

    private CompletionStage SetCompletedFlagAsync() throws ExternalDependencyException {
        try {
            return this.storageClient.updateAsync(SeedCollectionId, CompletedFlagKey, "true", "*");
        } catch (BaseException e) {
            log.error(String.format("%s,%s SetCompletedFlagAsync failed", SeedCollectionId, CompletedFlagKey));
            throw new ExternalDependencyException("SetCompletedFlagAsync failed");
        }
    }

    private CompletionStage SeedAsync(String template) throws ExternalDependencyException, InvalidInputException {
        String content = "";
        try (InputStream is = this.getClass().getResourceAsStream("data/default.json")) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line = reader.readLine();
                while (line != null) {
                    content += line;
                    line = reader.readLine();
                }
            }
        } catch (IOException e) {
            // Because travis test can not read file by now for some reason, Java.lang.Class can not read resource
            content = "{" +
                    "  \"Groups\": [" +
                    "    {" +
                    "      \"Id\": \"default_Chillers\"," +
                    "      \"DisplayName\": \"Chillers\"," +
                    "      \"Conditions\": [" +
                    "        {" +
                    "          \"Key\": \"properties.reported.Type\"," +
                    "          \"Operator\": \"EQ\"," +
                    "          \"Value\": \"Chiller\"" +
                    "        }" +
                    "      ]" +
                    "    }," +
                    "    {" +
                    "      \"Id\": \"default_PrototypingDevices\"," +
                    "      \"DisplayName\": \"Prototyping devices\"," +
                    "      \"Conditions\": [" +
                    "        {" +
                    "          \"Key\": \"properties.reported.Type\"," +
                    "          \"Operator\": \"EQ\"," +
                    "          \"Value\": \"Prototyping\"" +
                    "        }" +
                    "      ]" +
                    "    }," +
                    "    {" +
                    "      \"Id\": \"default_Engines\"," +
                    "      \"DisplayName\": \"Engines\"," +
                    "      \"Conditions\": [" +
                    "        {" +
                    "          \"Key\": \"properties.reported.Type\"," +
                    "          \"Operator\": \"EQ\"," +
                    "          \"Value\": \"Engine\"" +
                    "        }" +
                    "      ]" +
                    "    }," +
                    "    {" +
                    "      \"Id\": \"default_Trucks\"," +
                    "      \"DisplayName\": \"Trucks\"," +
                    "      \"Conditions\": [" +
                    "        {" +
                    "          \"Key\": \"properties.reported.Type\"," +
                    "          \"Operator\": \"EQ\"," +
                    "          \"Value\": \"Truck\"" +
                    "        }" +
                    "      ]" +
                    "    }," +
                    "    {" +
                    "      \"Id\": \"default_Elevators\"," +
                    "      \"DisplayName\": \"Elevators\"," +
                    "      \"Conditions\": [" +
                    "        {" +
                    "          \"Key\": \"properties.reported.Type\"," +
                    "          \"Operator\": \"EQ\"," +
                    "          \"Value\": \"Elevator\"" +
                    "        }" +
                    "      ]" +
                    "    }," +
                    "    {" +
                    "      \"Id\": \"default_AllDevices\"," +
                    "      \"DisplayName\": \"All devices\"," +
                    "      \"Conditions\": []" +
                    "    }" +
                    "  ]," +
                    "  \"Rules\": [" +
                    "    {" +
                    "      \"Id\": \"default_Chiller_Pressure_High\"," +
                    "      \"Name\": \"Chiller pressure too high\"," +
                    "      \"Enabled\": true," +
                    "      \"Description\": \"Pressure > 250\"," +
                    "      \"GroupId\": \"default_Chillers\"," +
                    "      \"Severity\": \"critical\"," +
                    "      \"Conditions\": [" +
                    "        {" +
                    "          \"Field\": \"pressure\"," +
                    "          \"Operator\": \"GreaterThan\"," +
                    "          \"Value\": \"250\"" +
                    "        }" +
                    "      ]" +
                    "    }," +
                    "    {" +
                    "      \"Id\": \"default_Prototyping_Temperature_High\"," +
                    "      \"Name\": \"Prototyping device temp too high\"," +
                    "      \"Enabled\": true," +
                    "      \"Description\": \"Temperature > 80 degrees\"," +
                    "      \"GroupId\": \"default_PrototypingDevices\"," +
                    "      \"Severity\": \"critical\"," +
                    "      \"Conditions\": [" +
                    "        {" +
                    "          \"Field\": \"temperature\"," +
                    "          \"Operator\": \"GreaterThan\"," +
                    "          \"Value\": \"80\"" +
                    "        }" +
                    "      ]" +
                    "    }," +
                    "    {" +
                    "      \"Id\": \"default_Engine_Fuel_Empty\"," +
                    "      \"Name\": \"Engine tank empty\"," +
                    "      \"Enabled\": true," +
                    "      \"Description\": \"Fuel level is less than 5\"," +
                    "      \"GroupId\": \"default_Engines\"," +
                    "      \"Severity\": \"info\"," +
                    "      \"Conditions\": [" +
                    "        {" +
                    "          \"Field\": \"fuel\"," +
                    "          \"Operator\": \"LessThan\"," +
                    "          \"Value\": \"5\"" +
                    "        }" +
                    "      ]" +
                    "    }," +
                    "    {" +
                    "      \"Id\": \"default_Truck_Temperature_High\"," +
                    "      \"Name\": \"Higher than normal cargo temperature\"," +
                    "      \"Enabled\": true," +
                    "      \"Description\": \"Cargo temperature is > 45 degrees\"," +
                    "      \"GroupId\": \"default_Trucks\"," +
                    "      \"Severity\": \"warning\"," +
                    "      \"Conditions\": [" +
                    "        {" +
                    "          \"Field\": \"cargotemperature\"," +
                    "          \"Operator\": \"GreaterThan\"," +
                    "          \"Value\": \"45\"" +
                    "        }" +
                    "      ]" +
                    "    }," +
                    "    {" +
                    "      \"Id\": \"default_Elevator_Vibration_Stopped\"," +
                    "      \"Name\": \"Elevator vibration stopped\"," +
                    "      \"Enabled\": true," +
                    "      \"Description\": \"Vibration < 0.1\"," +
                    "      \"GroupId\": \"default_Elevators\"," +
                    "      \"Severity\": \"warning\"," +
                    "      \"Conditions\": [" +
                    "        {" +
                    "          \"Field\": \"vibration\"," +
                    "          \"Operator\": \"LessThan\"," +
                    "          \"Value\": \"0.1\"" +
                    "        }" +
                    "      ]" +
                    "    }" +
                    "  ]," +
                    "  \"DeviceModels\": [" +
                    "    {" +
                    "      \"Id\": \"chiller-01\"," +
                    "      \"Count\": 1" +
                    "    }," +
                    "    {" +
                    "      \"Id\": \"chiller-02\"," +
                    "      \"Count\": 1" +
                    "    }," +
                    "    {" +
                    "      \"Id\": \"elevator-01\"," +
                    "      \"Count\": 1" +
                    "    }," +
                    "    {" +
                    "      \"Id\": \"elevator-02\"," +
                    "      \"Count\": 1" +
                    "    }," +
                    "    {" +
                    "      \"Id\": \"engine-01\"," +
                    "      \"Count\": 1" +
                    "    }," +
                    "    {" +
                    "      \"Id\": \"engine-02\"," +
                    "      \"Count\": 1" +
                    "    }," +
                    "    {" +
                    "      \"Id\": \"prototype-01\"," +
                    "      \"Count\": 1" +
                    "    }," +
                    "    {" +
                    "      \"Id\": \"prototype-02\"," +
                    "      \"Count\": 1" +
                    "    }," +
                    "    {" +
                    "      \"Id\": \"truck-01\"," +
                    "      \"Count\": 1" +
                    "    }," +
                    "    {" +
                    "      \"Id\": \"truck-02\"," +
                    "      \"Count\": 1" +
                    "    }" +
                    "  ]" +
                    "}";
        }
        return this.SeedSingleTemplateAsync(content);
    }

    private CompletionStage SeedSingleTemplateAsync(String content) throws InvalidInputException {
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
        StreamSupport.stream(template.getGroups().spliterator(), false).forEach(m -> {
            try {
                this.storage.updateDeviceGroupAsync(m.getId(), m, "*");
            } catch (Exception ex) {
                this.log.error(String.format("Failed to seed default group {group.DisplayName}", m.getDisplayName()));
            }
        });

        StreamSupport.stream(template.getRules().spliterator(), false).forEach(m -> {
            try {
                this.telemetryClient.UpdateRuleAsync(m, "*");
            } catch (Exception ex) {
                this.log.error(String.format("Failed to seed default rule %s", m.getDescription()));
            }
        });

        try {
            SimulationApiModel simulationModel = this.simulationClient.GetSimulationAsync().toCompletableFuture().get();

            if (simulationModel != null) {
                this.log.info("Skip seed simulation since there is already one simuation");
            } else {
                simulationModel = new SimulationApiModel(Lists.newArrayList(template.getDeviceModels()), "*", "1");
                this.simulationClient.UpdateSimulationAsync(simulationModel).toCompletableFuture().get();
            }
        } catch (Exception ex) {
            this.log.info("Failed to seed default simulation");
        }
        return CompletableFuture.runAsync(() -> {
        });
    }
}
