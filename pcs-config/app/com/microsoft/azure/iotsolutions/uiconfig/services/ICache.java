// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.BaseException;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.CacheValue;

import java.net.URISyntaxException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

@ImplementedBy(Cache.class)
public interface ICache {
    CompletionStage<CacheValue> GetCacheAsync();

    CompletionStage<CacheValue> SetCacheAsync(CacheValue cache) throws BaseException, ExecutionException, InterruptedException;

    CompletionStage RebuildCacheAsync(boolean force) throws BaseException, ExecutionException, InterruptedException, URISyntaxException;

    default public CompletionStage RebuildCacheAsync() throws InterruptedException, ExecutionException, BaseException, URISyntaxException {
        return RebuildCacheAsync(false);
    }
}
