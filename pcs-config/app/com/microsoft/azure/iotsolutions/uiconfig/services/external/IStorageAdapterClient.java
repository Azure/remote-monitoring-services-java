// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.*;

import java.util.concurrent.CompletionStage;

@ImplementedBy(StorageAdapterClient.class)
public interface IStorageAdapterClient {

    CompletionStage<ValueApiModel> getAsync(String collectionId, String key) throws BaseException;

    CompletionStage<ValueListApiModel> getAllAsync(String collectionId) throws BaseException;

    CompletionStage<ValueApiModel> createAsync(String collectionId, String value) throws BaseException;

    CompletionStage<ValueApiModel> updateAsync(String collectionId, String key, String value, String etag) throws BaseException;

    CompletionStage deleteAsync(String collectionId, String key) throws BaseException;
}

