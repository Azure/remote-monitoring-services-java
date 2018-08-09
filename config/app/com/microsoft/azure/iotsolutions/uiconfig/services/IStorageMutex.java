// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.BaseException;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

@ImplementedBy(StorageMutex.class)
public interface IStorageMutex {

    CompletionStage<Boolean> enterAsync(String collectionId, String key, int timeout) throws BaseException, ExecutionException, InterruptedException;

    CompletionStage leaveAsync(String collectionId, String key) throws BaseException;
}
