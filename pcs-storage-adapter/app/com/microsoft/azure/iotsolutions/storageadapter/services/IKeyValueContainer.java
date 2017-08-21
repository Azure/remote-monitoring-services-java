// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.documentdb.DocumentClientException;
import com.microsoft.azure.iotsolutions.storageadapter.services.models.ValueServiceModel;

import java.util.Iterator;

@ImplementedBy(DocDBKeyValueContainer.class)
public interface IKeyValueContainer {

    /**
     * Get a value by a given key
     *
     * @return the value
     */
    ValueServiceModel get(String collectionId, String key) throws DocumentClientException;

    /**
     * Get all key-value pairs in given collection
     *
     * @return List of key-value pairs
     */
    Iterator<ValueServiceModel> list(String collectionId);

    /**
     * Create key-value pair
     *
     * @return Created key-value pair
     */
    ValueServiceModel create(String collectionId, String key, ValueServiceModel input) throws DocumentClientException;

    /**
     * Update key-value pair (create if pair does not exist)
     *
     * @return Updated key-value pair
     */
    ValueServiceModel upsert(String collectionId, String key, ValueServiceModel input) throws DocumentClientException;

    /**
     * Delete key-value pair
     */
    void delete(String collectionId, String key) throws DocumentClientException;


    /**
     * Update key-value pair (create if pair does not exist)
     */
    Status ping();
}
