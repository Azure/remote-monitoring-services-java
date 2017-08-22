// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.DeviceGroupServiceModel;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.LogoServiceModel;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.concurrent.CompletionStage;

@ImplementedBy(Storage.class)
public interface IStorage {

    CompletionStage<Object> getThemeAsync();

    CompletionStage<Object> setThemeAsync(Object theme) throws UnsupportedEncodingException, URISyntaxException;

    CompletionStage<Object> getUserSetting(String id);

    CompletionStage<Object> setUserSetting(String id, Object setting) throws UnsupportedEncodingException, URISyntaxException;

    CompletionStage<LogoServiceModel> getLogoAsync();

    CompletionStage<LogoServiceModel> setLogoAsync(LogoServiceModel model) throws UnsupportedEncodingException, URISyntaxException;

    CompletionStage<Iterable<DeviceGroupServiceModel>> getAllDeviceGroupsAsync() throws UnsupportedEncodingException, URISyntaxException;

    CompletionStage<DeviceGroupServiceModel> getDeviceGroupAsync(String id) throws UnsupportedEncodingException, URISyntaxException;

    CompletionStage<DeviceGroupServiceModel> createDeviceGroupAsync(DeviceGroupServiceModel input) throws UnsupportedEncodingException, URISyntaxException;

    CompletionStage<DeviceGroupServiceModel> updateDeviceGroupAsync(String id, DeviceGroupServiceModel input, String etag) throws UnsupportedEncodingException, URISyntaxException;

    CompletionStage deleteDeviceGroupAsync(String id) throws UnsupportedEncodingException, URISyntaxException;
}
