// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.external;

import com.google.inject.ImplementedBy;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.concurrent.CompletionStage;

@ImplementedBy(StorageAdapterClient.class)
public interface IStorageAdapterClient {

    CompletionStage<ValueApiModel> getAsync(String collectionId, String key) throws UnsupportedEncodingException, URISyntaxException;

    CompletionStage<ValueListApiModel> getAllAsync(String collectionId) throws UnsupportedEncodingException, URISyntaxException;

    CompletionStage<ValueApiModel> createAsync(String collectionId, String value) throws UnsupportedEncodingException, URISyntaxException;

    CompletionStage<ValueApiModel> updateAsync(String collectionId, String key, String value, String etag) throws UnsupportedEncodingException, URISyntaxException;

    CompletionStage deleteAsync(String collectionId, String key) throws UnsupportedEncodingException, URISyntaxException;
}

