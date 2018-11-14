// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.BaseException;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.DeviceGroup;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.Logo;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.Package;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.ConfigTypeList;

import java.util.concurrent.CompletionStage;

@ImplementedBy(Storage.class)
public interface IStorage {

    CompletionStage<Object> getThemeAsync() throws BaseException;

    CompletionStage<Object> setThemeAsync(Object theme) throws BaseException;

    CompletionStage<Object> getUserSetting(String id) throws BaseException;

    CompletionStage<Object> setUserSetting(String id, Object setting) throws BaseException;

    CompletionStage<Logo> getLogoAsync() throws BaseException;

    CompletionStage<Logo> setLogoAsync(Logo model) throws BaseException;

    CompletionStage<Iterable<DeviceGroup>> getAllDeviceGroupsAsync() throws BaseException;

    CompletionStage<DeviceGroup> getDeviceGroupAsync(String id) throws BaseException;

    CompletionStage<DeviceGroup> createDeviceGroupAsync(DeviceGroup input) throws BaseException;

    CompletionStage<DeviceGroup> updateDeviceGroupAsync(String id, DeviceGroup input, String etag) throws BaseException;

    CompletionStage deleteDeviceGroupAsync(String id) throws BaseException;

    /**
     * Retrieves all packages that have been previous uploaded.
     * @return All packages which can be iterated over
     */
    CompletionStage<Iterable<Package>> getAllPackagesAsync() throws BaseException;

    /**
     * Retrieves all configurations that have been previous uploaded.
     * @return All configurations which can be iterated over
     */
    CompletionStage<ConfigTypeList> getAllConfigurationsAsync() throws BaseException;

    /**
     * Retrieves a single uploaded package by its unique Id.
     * @param id Unique identifier which was returned when creating a package
     * @return All packages which can be iterated over
     */
    CompletionStage<Package> getPackageAsync(String id) throws BaseException;

    /**
     * Creates a package with a new id given the provided input.
     * @param input {@link Package} parameters which include the name, content and type.
     * @return The created package along with id, and dateCreated.
     */
    CompletionStage<Package> addPackageAsync(Package input) throws BaseException;

    /**
     * Updates a previously created configurations.
     * @param customConfig The customConfig of the package to be maintained.
     */
    void updatePackageConfigsAsync(String customConfig) throws BaseException;

    /**
     * Deletes a previously uploaded package.
     * @param id The id of the package to be removed.
     */
    CompletionStage deletePackageAsync(String id) throws BaseException;
}
