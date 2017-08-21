// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.services.helpers;

public class QueryBuilder {
    public static String buildSQL(
            String CollectionId) {

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT * FROM c WHERE c.CollectionId = `" + CollectionId + "`");

        return queryBuilder.toString().replace('`', '"');
    }
}
