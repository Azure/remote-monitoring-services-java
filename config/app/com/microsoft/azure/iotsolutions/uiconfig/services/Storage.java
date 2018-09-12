// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.BaseException;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.ResourceNotFoundException;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.IStorageAdapterClient;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.ValueApiModel;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.DeviceGroup;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.Logo;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.Package;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.Theme;
import com.microsoft.azure.iotsolutions.uiconfig.services.runtime.IServicesConfig;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import play.libs.Json;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Singleton
public class Storage implements IStorage {

    private static final Logger.ALogger log = Logger.of(Storage.class);

    private static String SolutionCollectionId = "solution-settings";
    private static String ThemeKey = "theme";
    private static String LogoKey = "logo";
    private static String UserCollectionId = "user-settings";
    private static String DeviceGroupCollectionId = "devicegroups";
    private static String PackagesCollectionId = "packages";
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

    @Override
    public CompletionStage<Iterable<Package>> getAllPackagesAsync() throws BaseException {
        return this.client.getAllAsync(PackagesCollectionId).thenApplyAsync(p -> {
            return StreamSupport.stream(p.Items.spliterator(), false)
                    .map(Storage::createPackage)
                    .collect(Collectors.toList());
        });
    }

    @Override
    public CompletionStage<Package> getPackageAsync(String id) throws BaseException {
        return this.client.getAsync(PackagesCollectionId, id).thenApplyAsync(p -> {
           return Storage.createPackage(p);
        });
    }

    @Override
    public CompletionStage<Package> addPackageAsync(Package input) throws BaseException {
        String value = toJson(input);
        return client.createAsync(PackagesCollectionId, value).thenApplyAsync(p ->
            Storage.createPackage(p)
        );
    }

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

    private static Package createPackage(ValueApiModel input) {
        Package output = fromJson(input.getData(), Package.class);
        output.setId(input.getKey());
        return output;
    }

    private void appendAzureMapsKey(ObjectNode theme) {
        final String azureMapsKey = "AzureMapsKey";
        if (!theme.has(azureMapsKey)) {
            theme.put(azureMapsKey, config.getAzureMapsKey());
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
