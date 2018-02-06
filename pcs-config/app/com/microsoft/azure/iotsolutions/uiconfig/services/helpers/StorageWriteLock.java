// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.helpers;

import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.*;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.IStorageAdapterClient;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.ValueApiModel;
import play.Logger;
import play.libs.Json;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class StorageWriteLock<T> {

    private final String collectionId;
    private final String key;
    private final IStorageAdapterClient client;
    private final BiConsumer<T, Boolean> setLockFlagAction;
    private final Function<ValueApiModel, Boolean> testLockFunc;
    private static Logger.ALogger log = null;

    private Class<T> type;
    private T lastValue;
    private String lastETag;

    public StorageWriteLock(
            Class<T> type,
            IStorageAdapterClient client,
            String collectionId,
            String key,
            BiConsumer<T, Boolean> setLockFlagAction,
            Function<ValueApiModel, Boolean> testLockFunc) {
        this.client = client;
        this.collectionId = collectionId;
        this.key = key;
        this.setLockFlagAction = setLockFlagAction;
        this.testLockFunc = testLockFunc;
        this.lastETag = null;
        this.type = type;
        log = Logger.of(this.getClass());
    }

    private CompletionStage<String> updateValueAsync(T value, String etag) throws BaseException {
        return this.client.updateAsync(
                this.collectionId,
                this.key,
                Json.stringify(Json.toJson(value)),
                etag).thenApplyAsync(m -> m.getETag());
    }

    public CompletionStage<Optional<Boolean>> tryLockAsync() throws ResourceOutOfDateException, ExternalDependencyException {
        if (this.lastETag != null) {
            throw new ResourceOutOfDateException("Lock has already been acquired");
        }

        ValueApiModel model = this.getLock();

        if (!this.testLockFunc.apply(model)) {
            return CompletableFuture.supplyAsync(() -> Optional.of(false));
        }
        try {
            this.lastValue = model == null ? type.newInstance() : Json.fromJson(Json.parse(model.getData()), type);
        } catch (InstantiationException | IllegalAccessException e) {
            String message = String.format("Lock failed when creating new instance type " + type.getTypeName());
            log.error(message, e);
            throw new ExternalDependencyException(message);
        }
        this.setLockFlagAction.accept(this.lastValue, true);

        return updateLock(model);
    }

    public CompletionStage releaseAsync() throws ResourceOutOfDateException, ExternalDependencyException {
        if (this.lastETag == null) {
            throw new ResourceOutOfDateException("Lock was not acquired yet");
        }

        this.setLockFlagAction.accept(this.lastValue, false);

        try {
            return this.updateValueAsync(this.lastValue, this.lastETag).thenAcceptAsync(m -> {
            });
        } catch (ResourceNotFoundException e) {
            // Nothing to do
        } catch (Exception e) {
            String errorMessage = String.format("Unexpected error when releasing lock %s,%s", this.collectionId, this.key);
            log.error(errorMessage, e);
            throw new ExternalDependencyException(errorMessage, e);
        }
        this.lastETag = null;
        return CompletableFuture.completedFuture(null);
    }

    public CompletionStage<Boolean> writeAndReleaseAsync(T newValue) throws ResourceOutOfDateException {
        if (this.lastETag == null) {
            throw new ResourceOutOfDateException("Lock was not acquired yet");
        }

        this.setLockFlagAction.accept(newValue, false);
        try {
            return this.updateValueAsync(newValue, this.lastETag).thenApplyAsync(m -> {
                this.lastETag = null;
                return true;
            });
        } catch (BaseException e) {
            return CompletableFuture.completedFuture(false);
        }
    }

    private ValueApiModel getLock() throws ExternalDependencyException {
        try {
            return this.client.getAsync(this.collectionId, this.key).toCompletableFuture().get();
        } catch (ResourceNotFoundException e) {
            // Nothing to do
        } catch (InterruptedException | ExecutionException | BaseException e) {
            String errorMessage = String.format("Unexpected error while locking %s,%s", this.collectionId, this.key);
            this.log.error(errorMessage, e);
            throw new ExternalDependencyException(errorMessage);
        }
        return null;
    }

    private CompletionStage<Optional<Boolean>> updateLock(ValueApiModel lock) {
        try {
            return this.updateValueAsync(this.lastValue, lock == null ? null : lock.getETag()).thenAccept(m -> {
                this.lastETag = m;
            }).thenApplyAsync((m) -> Optional.of(true));
        } catch (BaseException e) {
            return CompletableFuture.completedFuture(null);
        }
    }
}
