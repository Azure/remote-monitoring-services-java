// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.services.runtime;

import com.google.inject.ImplementedBy;

@ImplementedBy(ServicesConfig.class)
public interface IServicesConfig {

    /**
     * Get Document connection string.
     *
     * @return Connection string
     */
    String getDocumentDBConnectionString();

    /**
     * Get Container Name
     *
     * @return Container Name string
     */
    String getContainerName();
}
