// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.BaseException;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.ResourceNotFoundException;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.IStorageAdapterClient;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.PackageValidation.IPackageValidator;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.PackageValidation.PackageValidatorFactory;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.ValueApiModel;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.*;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.PackageServiceModel;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.IServicesConfig;
import com.microsoft.azure.sdk.iot.service.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import play.Logger;
import play.libs.Json;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.joda.time.format.DateTimeFormat.forPattern;

@Singleton
public class Storage implements IStorage {

    private static final Logger.ALogger log = Logger.of(Storage.class);

    private static String SolutionCollectionId = "solution-settings";
    private static String ThemeKey = "theme";
    private static String LogoKey = "logo";
    private static String UserCollectionId = "user-settings";
    private static String DeviceGroupCollectionId = "devicegroups";
    private static String PackagesCollectionId = "packages";
    private static final String PackagesConfigTypeKey = "config-types";
    private static final DateTimeFormatter DATE_FORMAT =
            forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");
    private static final String AzureMapsKey = "AzureMapsKey";
    private final IStorageAdapterClient client;
    private final IServicesConfig config;

    @Inject
    public Storage(IStorageAdapterClient client, IServicesConfig config) {
        this.client = client;
        this.config = config;
    }

    private static <T> String toJson(T o) {
        return Json.stringify(Json.toJson(o));
    }

    private static <A> A fromJson(String json, Class<A> clazz) {
        return Json.fromJson(Json.parse(json), clazz);
    }

    @Override
    public CompletionStage<Object> getThemeAsync() {
        String data = toJson(Theme.Default);
        try {
            String serverData = client.getAsync(SolutionCollectionId, ThemeKey).toCompletableFuture().get().getData();
            if (serverData != null && StringUtils.isNotBlank(serverData)) {
                data = serverData;
            }
        } catch (Exception ex) {
        }
        ObjectNode themeOut = (ObjectNode) Json.parse(data);
        appendAzureMapsKey(themeOut);
        return CompletableFuture.supplyAsync(() -> fromJson(themeOut.toString(), Object.class));
    }

    @Override
    public CompletionStage<Object> setThemeAsync(Object themeIn) throws BaseException {

        String value = "";
        try {
            value = toJson(themeIn);
        } catch (Exception e) {
        }

        return client.updateAsync(SolutionCollectionId, ThemeKey, value, "*").thenApplyAsync(m -> {
                    String data = "{}";
                    if (m.getData() != null && StringUtils.isNotBlank(m.getData())) {
                        data = m.getData();
                    }
                    ObjectNode themeOut = (ObjectNode) Json.parse(data);
                    appendAzureMapsKey(themeOut);
                    return fromJson(themeOut.toString(), Object.class);
                }
        );
    }

    @Override
    public CompletionStage<Object> getUserSetting(String id) {
        try {
            return client.getAsync(UserCollectionId, id).thenApplyAsync(m ->
                    fromJson(m.getData(), Object.class)
            );
        } catch (Exception ex) {
            return CompletableFuture.supplyAsync(() -> new Object());
        }
    }

    @Override
    public CompletionStage<Object> setUserSetting(String id, Object setting) throws BaseException {
        String value = toJson(setting);
        return client.updateAsync(UserCollectionId, id, value, "*").thenApplyAsync(m ->
                fromJson(m.getData(), Object.class)
        );
    }

    @Override
    public CompletionStage<Logo> getLogoAsync() {
        try {
            return client.getAsync(SolutionCollectionId, LogoKey)
                    .handle((m, error) -> {
                        if (error != null) {
                            return Logo.Default;
                        } else {
                            return fromJson(m.getData(), Logo.class);
                        }
                    });
        } catch (ResourceNotFoundException ex) {
            log.debug("Could not find logo, returning default logo");
            return CompletableFuture.supplyAsync(() -> Logo.Default);
        } catch (BaseException ex) {
            throw new CompletionException("Unable to get logo", ex);
        }
    }

    @Override
    public CompletionStage<Logo> setLogoAsync(Logo model) throws BaseException {
        if (model.getName() == null || model.getImage() == null) {
            try {
                return this.getLogoAsync().thenComposeAsync(current -> {
                    try {
                        updateLogoWithCurrent(model, current);
                        return updateLogoAsync(toJson(model));
                    } catch (BaseException be) {
                        throw new CompletionException("Cannot update logo", be);
                    }
                });
            } catch (Exception e) {
                log.error("Exception on getLogoAsync: ", e.toString());
            }
        }
        return updateLogoAsync(toJson(model));
    }

    @Override
    public CompletionStage<Iterable<DeviceGroup>> getAllDeviceGroupsAsync() throws BaseException {
        return client.getAllAsync(DeviceGroupCollectionId).thenApplyAsync(m -> {
            return StreamSupport.stream(m.Items.spliterator(), false)
                                .map(Storage::createGroup)
                                .collect(Collectors.toList());
        });
    }

    @Override
    public CompletionStage<DeviceGroup> getDeviceGroupAsync(String id) throws BaseException {
        return client.getAsync(DeviceGroupCollectionId, id).thenApplyAsync(m -> {
            return createGroup(m);
        });
    }

    @Override
    public CompletionStage<DeviceGroup> createDeviceGroupAsync(DeviceGroup input) throws BaseException {
        String value = toJson(input);
        return client.createAsync(DeviceGroupCollectionId, value).thenApplyAsync(m ->
                createGroup(m)
        );
    }

    @Override
    public CompletionStage<DeviceGroup> updateDeviceGroupAsync(String id, DeviceGroup input, String etag) throws BaseException {
        String value = toJson(input);
        return client.updateAsync(DeviceGroupCollectionId, id, value, etag).thenApplyAsync(m ->
                createGroup(m)
        );
    }

    @Override
    public CompletionStage deleteDeviceGroupAsync(String id) throws BaseException {
        return client.deleteAsync(DeviceGroupCollectionId, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletionStage<Iterable<PackageServiceModel>> getAllPackagesAsync() throws BaseException {
        return this.client.getAllAsync(PackagesCollectionId).thenApplyAsync(p -> {
            return StreamSupport.stream(p.Items.spliterator(), false)
                    .filter(pckg -> !(pckg.getKey().equals(PackagesConfigTypeKey)))
                    .map(Storage::createPackage)
                    .collect(Collectors.toList());
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletionStage<Iterable<PackageServiceModel>> getFilteredPackagesAsync(String packageType, String configType)
            throws BaseException, ExecutionException, InterruptedException {
        CompletionStage<Iterable<PackageServiceModel>> packages =  this.client.getAllAsync(PackagesCollectionId).thenApplyAsync(p ->
        {
            return StreamSupport.stream(p.Items.spliterator(), false)
                    .filter(pckg -> !(pckg.getKey().equals(PackagesConfigTypeKey)))
                    .map(Storage::createPackage)
                    .collect(Collectors.toList());
        });

        boolean isPackageTypeEmpty = StringUtils.isBlank(packageType);
        boolean isConfigTypeEmpty = StringUtils.isBlank(configType);

        if (!isPackageTypeEmpty && !isConfigTypeEmpty)
        {
            return CompletableFuture.completedFuture(StreamSupport.stream(
                    packages.toCompletableFuture().get().spliterator(),
                    false)
                    .filter(pckg -> (
                            pckg.getPackageType().toString().equals(packageType) &&
                            pckg.getConfigType().equals(configType))
                    )
                    .collect(Collectors.toList()));
        }
        else if (!isPackageTypeEmpty && isConfigTypeEmpty)
        {
            return CompletableFuture.completedFuture(StreamSupport.stream(
                    packages.toCompletableFuture().get().spliterator(),
                    false)
                    .filter(pckg -> (
                            pckg.getPackageType().toString().equals(packageType)
                    )).collect(Collectors.toList()));
        }
        else if (isPackageTypeEmpty && !isConfigTypeEmpty)
        {
            // Non-empty ConfigType with empty PackageType indicates invalid packages
            throw new InvalidInputException("Package Type cannot be empty.");
        }
        else
        {
            // Return all packages when ConfigType & PackageType are empty.
            return packages;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletionStage<ConfigTypeListServiceModel> getAllConfigTypesAsync() throws BaseException {
        try {
            return this.client.getAsync(PackagesCollectionId, PackagesConfigTypeKey).thenApplyAsync(p -> {
                return fromJson(p.getData(), ConfigTypeListServiceModel.class);
            });
        } catch (ResourceNotFoundException e) {
            log.debug("Document config-types has not been created.");
            // Return empty list of configTypes.
            return CompletableFuture.completedFuture(new ConfigTypeListServiceModel());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletionStage<PackageServiceModel> getPackageAsync(String id) throws BaseException {
        return this.client.getAsync(PackagesCollectionId, id).thenApplyAsync(p -> {
            return Storage.createPackage(p);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletionStage<PackageServiceModel> addPackageAsync(PackageServiceModel input) throws
            BaseException,
            ExecutionException,
            InterruptedException{

        boolean isValidPackage = this.isValidPackage(input);
        if (!isValidPackage) {
            String msg = String.format("Package provided is a invalid deployment manifest " +
                         "for packageType %s", input.getPackageType());

            msg = input.getPackageType().equals(PackageType.deviceConfiguration) ?
                         String.format("and configuration type %s", input.getConfigType()) : StringUtils.EMPTY;;

            throw new InvalidInputException(msg);
        }

        try {
            Configuration config = fromJson(input.getContent(), Configuration.class);
            if (config.getContent() == null) {
                throw new InvalidInputException("Manifest provided is valid json but not a valid manifest");
            }
        } catch(Exception e) {
            final String message = "Package provided is not a valid deployment manifest";
            log.error(message, e);
            throw new InvalidInputException(message);
        }

        input.setDateCreated(Storage.DATE_FORMAT.print(DateTime.now().toDateTime(DateTimeZone.UTC)));
        final String value = toJson(input);

        CompletionStage<PackageServiceModel> result = client.createAsync(PackagesCollectionId, value)
                .thenApplyAsync(p ->
                    Storage.createPackage(p)
                );

        if (!(StringUtils.isBlank(input.getConfigType())) &&
            input.getPackageType().equals(PackageType.deviceConfiguration)) {
            this.updateConfigTypeAsync(input.getConfigType());
        }

        return result;
    }

    public void updateConfigTypeAsync(String configType) throws
            BaseException,
            ExecutionException,
            InterruptedException {
            ConfigTypeListServiceModel list;

        try {
            CompletionStage<ConfigTypeListServiceModel> configs = this.client.getAsync(
                    PackagesCollectionId,
                    PackagesConfigTypeKey)
                    .thenApplyAsync(p -> {
                        return fromJson(p.getData(), ConfigTypeListServiceModel.class);
                    });
            list = configs.toCompletableFuture().get();
        } catch(ResourceNotFoundException e) {
            log.debug("Config Types have not been created.");
            // Return empty Config types
            list = new ConfigTypeListServiceModel();
        }

        list.add(configType);
        client.updateAsync(PackagesCollectionId, PackagesConfigTypeKey, toJson(list), "*");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletionStage deletePackageAsync(String id) throws BaseException {
        return client.deleteAsync(PackagesCollectionId, id);
    }

    private static DeviceGroup createGroup(ValueApiModel input) {
        DeviceGroup output = fromJson(input.getData(), DeviceGroup.class);
        output.setId(input.getKey());
        output.setETag(input.getETag());
        return output;
    }

    private static PackageServiceModel createPackage(ValueApiModel input) {
        PackageServiceModel output = fromJson(input.getData(), PackageServiceModel.class);
        output.setId(input.getKey());
        return output;
    }

    private Boolean isValidPackage(PackageServiceModel input) {
        IPackageValidator validator = PackageValidatorFactory.GetValidator(
                input.getPackageType(),
                input.getConfigType());
        // Bypass validation for custom config type
        return validator == null || validator.validate();
    }

    private void appendAzureMapsKey(ObjectNode theme) {
        if (!theme.has(AzureMapsKey)) {
            theme.put(AzureMapsKey, config.getAzureMapsKey());
        }
    }

    private Logo updateLogoWithCurrent(Logo model, Logo current) {
        if (!current.getDefault()) {
            String currentName = current.getName();
            if (model.getName() == null && currentName != null) {
                model.setName(currentName);
            }
            String currentImage = current.getImage();
            if (model.getImage() == null && currentImage != null) {
                model.setImage(currentImage);
                model.setType(current.getType());
            }
        }
        return model;
    }

    private CompletionStage<Logo> updateLogoAsync(String value) throws BaseException {
        return client.updateAsync(SolutionCollectionId, LogoKey, value, "*").thenApplyAsync(m ->
                fromJson(m.getData(), Logo.class)
        );
    }
}
