// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.BaseException;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.DeviceGroup;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.Logo;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.Package;

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

    CompletionStage<Iterable<Package>> getAllPackagesAsync() throws BaseException;

    CompletionStage<Package> getPackageAsync(String id) throws BaseException;

    CompletionStage<Package> addPackageAsync(Package input) throws BaseException;

    CompletionStage<Package> deletePackageAsync(String id) throws BaseException;
}
