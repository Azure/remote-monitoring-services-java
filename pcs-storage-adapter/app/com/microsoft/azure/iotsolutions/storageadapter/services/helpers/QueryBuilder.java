// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.services.helpers;

import com.microsoft.azure.documentdb.SqlQuerySpec;
import com.microsoft.azure.iotsolutions.storageadapter.services.exceptions.InvalidInputException;

public class QueryBuilder {

    private static final String INVALID_CHARACTER = "[^A-Za-z0-9:;.,_-]";

    public static SqlQuerySpec buildQuerySpec(String CollectionId) throws InvalidInputException {
        validate(CollectionId);
        SqlQuerySpec querySpec = new SqlQuerySpec("SELECT * FROM c WHERE c.CollectionId = @collectionId");
        querySpec.set("@collectionId", CollectionId);
        return querySpec;
    }

    private static void validate(String input) throws InvalidInputException {
        input = input.trim();
        if (input.split(INVALID_CHARACTER, 2).length > 1) {
            throw new InvalidInputException("input '" + input + "' contains invalid characters.");
        }
    }
}
