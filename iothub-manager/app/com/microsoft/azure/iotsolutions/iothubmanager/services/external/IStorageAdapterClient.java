// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.external;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.iothubmanager.services.exceptions.*;

import java.util.concurrent.CompletionStage;

@ImplementedBy(StorageAdapterClient.class)
public interface IStorageAdapterClient {

    CompletionStage<ValueApiModel> getAsync(String collectionId, String key)
        throws ResourceNotFoundException, ConflictingResourceException, ExternalDependencyException;

    CompletionStage<ValueListApiModel> getAllAsync(String collectionId)
        throws ResourceNotFoundException, ConflictingResourceException, ExternalDependencyException;

    CompletionStage<ValueApiModel> createAsync(String collectionId, String value)
        throws ResourceNotFoundException, ConflictingResourceException, ExternalDependencyException;

    CompletionStage<ValueApiModel> updateAsync(String collectionId, String key, String value, String etag)
        throws ResourceNotFoundException, ConflictingResourceException, ExternalDependencyException;

    CompletionStage deleteAsync(String collectionId, String key)
        throws ResourceNotFoundException, ConflictingResourceException, ExternalDependencyException;
}

