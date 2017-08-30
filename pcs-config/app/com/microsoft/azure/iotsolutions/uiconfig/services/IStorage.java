// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.*;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.DeviceGroupServiceModel;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.LogoServiceModel;

import java.util.concurrent.CompletionStage;

@ImplementedBy(Storage.class)
public interface IStorage {

    CompletionStage<Object> getThemeAsync() throws BaseException;

    CompletionStage<Object> setThemeAsync(Object theme) throws BaseException;

    CompletionStage<Object> getUserSetting(String id) throws BaseException;

    CompletionStage<Object> setUserSetting(String id, Object setting) throws BaseException;

    CompletionStage<LogoServiceModel> getLogoAsync() throws BaseException;

    CompletionStage<LogoServiceModel> setLogoAsync(LogoServiceModel model) throws BaseException;

    CompletionStage<Iterable<DeviceGroupServiceModel>> getAllDeviceGroupsAsync() throws BaseException;

    CompletionStage<DeviceGroupServiceModel> getDeviceGroupAsync(String id) throws BaseException;

    CompletionStage<DeviceGroupServiceModel> createDeviceGroupAsync(DeviceGroupServiceModel input) throws BaseException;

    CompletionStage<DeviceGroupServiceModel> updateDeviceGroupAsync(String id, DeviceGroupServiceModel input, String etag) throws BaseException;

    CompletionStage deleteDeviceGroupAsync(String id) throws BaseException;
}
