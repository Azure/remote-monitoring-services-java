// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.BaseException;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.DeviceGroup;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.Logo;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.PackageServiceModel;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.ConfigTypeListServiceModel;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

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
    CompletionStage<Iterable<PackageServiceModel>> getAllPackagesAsync() throws BaseException;

    /**
     * Retrieves packages based on parameters provided.
     * @return All packages which can be iterated over
     */
    CompletionStage<Iterable<PackageServiceModel>> getFilteredPackagesAsync(String packageType, String configType)
            throws BaseException, ExecutionException, InterruptedException;

    /**
     * Retrieves all configtypes that have been previous uploaded.
     * @return All configtypes which can be iterated over
     */
    CompletionStage<ConfigTypeListServiceModel> getAllConfigTypesAsync() throws BaseException;

    /**
     * Retrieves a single uploaded package by its unique Id.
     * @param id Unique identifier which was returned when creating a package
     * @return All packages which can be iterated over
     */
    CompletionStage<PackageServiceModel> getPackageAsync(String id) throws BaseException;

    /**
     * Creates a package with a new id given the provided input.
     * @param input {@link PackageServiceModel} parameters which include the name, content and type.
     * @return The created package along with id, and dateCreated.
     */
    CompletionStage<PackageServiceModel> addPackageAsync(PackageServiceModel input) throws
            BaseException,
            ExecutionException,
            InterruptedException;

    /**
     * Updates a previously created configurations.
     * @param configTypes The customConfig of the package to be maintained.
     */
    void updateConfigTypeAsync(String configTypes) throws
            BaseException,
            ExecutionException,
            InterruptedException;

    /**
     * Deletes a previously uploaded package.
     * @param id The id of the package to be removed.
     */
    CompletionStage deletePackageAsync(String id) throws BaseException;
}
