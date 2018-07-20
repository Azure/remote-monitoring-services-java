// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.webservice.wrappers;

public class GuidKeyGenerator implements IKeyGenerator {
    public String generate() {
        return java.util.UUID.randomUUID().toString();
    }
}
