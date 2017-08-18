// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.services.wrappers;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.documentdb.DocumentClientException;
import com.microsoft.azure.iotsolutions.storageadapter.services.exceptions.CreateResourceException;

@ImplementedBy(DocumentClientFactory.class)
public interface IFactory<T> {
    T Create() throws DocumentClientException, CreateResourceException;
}

