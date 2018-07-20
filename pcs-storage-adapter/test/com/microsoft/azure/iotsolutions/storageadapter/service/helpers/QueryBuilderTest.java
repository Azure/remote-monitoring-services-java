// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.storageadapter.service.helpers;

import com.microsoft.azure.documentdb.SqlQuerySpec;
import com.microsoft.azure.iotsolutions.storageadapter.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.storageadapter.services.helpers.QueryBuilder;
import helpers.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

public class QueryBuilderTest {

    @Test(timeout = 5000)
    @Category({UnitTest.class})
    public void buildQuerySpecTest() throws InvalidInputException {
        SqlQuerySpec querySpec = QueryBuilder.buildQuerySpec("collection:;.,_-1");
        assertEquals(querySpec.toJson(), "{\"query\":\"SELECT * FROM c WHERE c.CollectionId = @collectionId\",\"@collectionId\":\"collection:;.,_-1\"}");
    }

    @Test(timeout = 500000, expected = InvalidInputException.class)
    @Category({UnitTest.class})
    public void buildQuerySpecWithInvalidInputTest() throws InvalidInputException {
        QueryBuilder.buildQuerySpec("' and 1=1");
    }

}
