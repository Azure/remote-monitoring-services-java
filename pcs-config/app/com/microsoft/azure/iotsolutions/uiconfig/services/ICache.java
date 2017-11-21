// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.BaseException;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.CacheValue;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

@ImplementedBy(Cache.class)
public interface ICache {
    CompletionStage<CacheValue> getCacheAsync();

    CompletionStage<CacheValue> setCacheAsync(CacheValue cache) throws BaseException, ExecutionException, InterruptedException;

    CompletionStage rebuildCacheAsync(boolean force) throws Exception;

    default CompletionStage rebuildCacheAsync() throws Exception {
        return rebuildCacheAsync(false);
    }
}
