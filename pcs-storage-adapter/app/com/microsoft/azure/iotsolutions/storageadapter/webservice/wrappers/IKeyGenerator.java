// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.webservice.wrappers;

import com.google.inject.ImplementedBy;

@ImplementedBy(GuidKeyGenerator.class)
public interface IKeyGenerator {
    String generate();
}
