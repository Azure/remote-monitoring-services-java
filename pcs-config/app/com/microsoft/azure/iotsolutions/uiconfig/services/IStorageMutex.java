// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.BaseException;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

@ImplementedBy(StorageMutex.class)
public interface IStorageMutex {

    CompletionStage<Boolean> EnterAsync(String collectionId, String key, int timeout) throws BaseException, ExecutionException, InterruptedException;

    CompletionStage LeaveAsync(String collectionId, String key) throws BaseException;
}
