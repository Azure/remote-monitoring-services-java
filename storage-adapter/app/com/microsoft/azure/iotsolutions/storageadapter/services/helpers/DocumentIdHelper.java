// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.services.helpers;

public class DocumentIdHelper {
    public static String GenerateId(String collectionId, String key) {
        return collectionId.toLowerCase() + "." + key.toLowerCase();
    }
}
