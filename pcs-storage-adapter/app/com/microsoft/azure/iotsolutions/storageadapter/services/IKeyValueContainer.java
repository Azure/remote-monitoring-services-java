// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.services;

import com.google.inject.ImplementedBy;

@ImplementedBy(DocDBKeyValueContainer.class)
public interface IKeyValueContainer {


    /**
     * Update key-value pair (create if pair does not exist)
     */
    Status ping();
}
