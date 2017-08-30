// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.*;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.IStorageAdapterClient;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.ValueApiModel;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.DeviceGroupServiceModel;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.LogoServiceModel;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.ThemeServiceModel;
import play.libs.Json;

import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Singleton
public class Storage implements IStorage {

    static String SolutionCollectionId = "solution-settings";
    static String ThemeKey = "theme";
    static String LogoKey = "logo";
    static String UserCollectionId = "user-settings";
    static String DeviceGroupCollectionId = "deviceGroups";
    private final IStorageAdapterClient client;

    @Inject
    public Storage(IStorageAdapterClient client) {
        this.client = client;
    }

    private static <T> String toJson(T o) {
        return Json.stringify(Json.toJson(o));
    }

    private static <A> A fromJson(String json, Class<A> clazz) {
        return Json.fromJson(Json.parse(json), clazz);
    }

    @Override
    public CompletionStage<Object> getThemeAsync() {
        try {
            return client.getAsync(SolutionCollectionId, ThemeKey).thenApplyAsync(m ->
                fromJson(m.getData(), Object.class)
            );
        } catch (Exception ex) {
            return CompletableFuture.supplyAsync(() -> ThemeServiceModel.Default);
        }
    }

    @Override
    public CompletionStage<Object> setThemeAsync(Object theme) throws BaseException {
        String value = toJson(theme);
        return client.updateAsync(SolutionCollectionId, ThemeKey, value, "*").thenApplyAsync(m ->
            fromJson(m.getData(), Object.class)
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
    public CompletionStage<LogoServiceModel> getLogoAsync() {
        try {
            return client.getAsync(SolutionCollectionId, LogoKey)
                .handle((m, error) -> {
                    if (error != null) {
                        return LogoServiceModel.Default;
                    } else {
                        return fromJson(m.getData(), LogoServiceModel.class);
                    }
                });
        } catch (BaseException ex) {
            throw new CompletionException("Unable to get logo", ex);
        }
    }

    @Override
    public CompletionStage<LogoServiceModel> setLogoAsync(LogoServiceModel model) throws BaseException {
        String value = toJson(model);
        return client.updateAsync(SolutionCollectionId, LogoKey, value, "*").thenApplyAsync(m ->
            fromJson(m.getData(), LogoServiceModel.class)
        );
    }

    @Override
    public CompletionStage<Iterable<DeviceGroupServiceModel>> getAllDeviceGroupsAsync() throws BaseException {
        return client.getAllAsync(DeviceGroupCollectionId).thenApplyAsync(m -> {
            return StreamSupport.stream(m.Items.spliterator(), false).map(Storage::createGroupServiceModel).collect(Collectors.toList());
        });
    }

    @Override
    public CompletionStage<DeviceGroupServiceModel> getDeviceGroupAsync(String id) throws BaseException {
        return client.getAsync(DeviceGroupCollectionId, id).thenApplyAsync(m -> {
            return createGroupServiceModel(m);
        });
    }

    @Override
    public CompletionStage<DeviceGroupServiceModel> createDeviceGroupAsync(DeviceGroupServiceModel input) throws BaseException {
        String value = toJson(input);
        return client.createAsync(DeviceGroupCollectionId, value).thenApplyAsync(m ->
            createGroupServiceModel(m)
        );
    }

    @Override
    public CompletionStage<DeviceGroupServiceModel> updateDeviceGroupAsync(String id, DeviceGroupServiceModel input, String etag) throws BaseException {
        String value = toJson(input);
        return client.updateAsync(DeviceGroupCollectionId, id, value, etag).thenApplyAsync(m ->
            createGroupServiceModel(m)
        );
    }

    @Override
    public CompletionStage deleteDeviceGroupAsync(String id) throws BaseException {
        return client.deleteAsync(DeviceGroupCollectionId, id);
    }

    private static DeviceGroupServiceModel createGroupServiceModel(ValueApiModel input) {
        DeviceGroupServiceModel output = fromJson(input.getData(), DeviceGroupServiceModel.class);
        output.setId(input.getKey());
        output.setETag(input.getETag());
        return output;
    }
}
