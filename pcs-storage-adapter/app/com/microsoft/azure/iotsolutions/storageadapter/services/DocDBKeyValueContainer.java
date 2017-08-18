// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.services;

import com.google.inject.Inject;
import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.documentdb.DocumentClientException;
import com.microsoft.azure.iotsolutions.storageadapter.services.exceptions.CreateResourceException;
import com.microsoft.azure.iotsolutions.storageadapter.services.runtime.IServicesConfig;
import com.microsoft.azure.iotsolutions.storageadapter.services.wrappers.IFactory;
import play.Logger;

import java.net.URI;

public class DocDBKeyValueContainer implements IKeyValueContainer {

    private static final Logger.ALogger log = Logger.of(DocDBKeyValueContainer.class);
    private DocumentClient client;
    // Todo: Rename colUrl
    private String colUrl;

    @Inject
    public DocDBKeyValueContainer(IFactory<DocumentClient> clientFactory,
                                  final IServicesConfig config) throws DocumentClientException, CreateResourceException {
        this.client = clientFactory.Create();
        this.colUrl = config.getContainerName();
    }


    public Status ping() {
        URI response = null;
        if (this.client != null) {
            response = this.client.getReadEndpoint();
        }
        if (response != null) {
            return new Status(true, "Alive and Well!");
        } else {
            return new Status(false, "Could not connect to DocumentDb." + colUrl);
        }
    }

}
