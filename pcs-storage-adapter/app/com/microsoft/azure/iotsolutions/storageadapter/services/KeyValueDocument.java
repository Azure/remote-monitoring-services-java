// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.services;

import com.microsoft.azure.iotsolutions.storageadapter.services.helpers.DocumentIdHelper;

class KeyValueDocument {
    public String id;
    public String CollectionId;
    public String Key;
    public String Data;

    public KeyValueDocument(String collectionId, String key, String data) {
        id = DocumentIdHelper.GenerateId(collectionId, key);
        CollectionId = collectionId;
        Key = key;
        Data = data;
    }
}
