// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services;

import com.google.inject.Inject;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.BaseException;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.ConflictingResourceException;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.ResourceNotFoundException;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.IStorageAdapterClient;
import com.microsoft.azure.iotsolutions.uiconfig.services.external.ValueApiModel;
import org.joda.time.DateTime;
import play.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

public class StorageMutex implements IStorageMutex {

    private final String LastModifiedKey = "$modified";
    private IStorageAdapterClient storageClient;
    private static final Logger.ALogger log = Logger.of(StorageMutex.class);

    @Inject
    public StorageMutex(IStorageAdapterClient storageClient) {
        this.storageClient = storageClient;
    }

    public CompletionStage<Boolean> enterAsync(String collectionId, String key, int timeout) throws InterruptedException, BaseException, ExecutionException {
        String etag = null;
        while (true) {
            try {
                ValueApiModel model = this.storageClient.getAsync(collectionId, key).toCompletableFuture().get();
                etag = model.getETag();

                // Mutex was captured by some other instance, return `false` except the state was not updated for a long time
                // The motivation of timeout check is to recovery from stale state due to instance crash
                if (Boolean.parseBoolean(model.getData())) {
                    DateTime lastModified = new DateTime(1970, 1, 1, 0, 0);
                    if (model.getMetadata().containsKey(LastModifiedKey)) {
                        try {
                            lastModified = DateTime.parse(model.getMetadata().get(LastModifiedKey));
                        } catch (IllegalArgumentException e) {
                        }
                    }

                    if (lastModified.plusSeconds(timeout).isAfterNow()) {
                        return CompletableFuture.supplyAsync(() -> new Boolean(false));
                    }
                }
            } catch (ResourceNotFoundException e) {
                log.info(String.format("EnterAsync %s %s  not found ", collectionId, key));
            }

            try {
                // In case there is no such a mutex, the `etag` will be null. It will cause
                // a new mutex created, and the operation will be synchronized
                this.storageClient.updateAsync(collectionId, key, "true", etag).toCompletableFuture().get();
                // Successfully enter the mutex, return `true`
                return CompletableFuture.supplyAsync(() -> new Boolean(true));
            } catch (ConflictingResourceException e) {
                // Etag does not match. Restart the whole process
                log.info(String.format("Conflicted  %s %s :Etag does not match. Restart the whole process ", collectionId, key));
            }
        }
    }

    public CompletionStage leaveAsync(String collectionId, String key) throws BaseException {
        return this.storageClient.updateAsync(collectionId, key, "false", "*");
    }
}
