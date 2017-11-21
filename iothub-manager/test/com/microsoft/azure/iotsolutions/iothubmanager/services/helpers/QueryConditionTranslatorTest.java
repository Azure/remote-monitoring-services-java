// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.helpers;

import helpers.UnitTest;
import org.junit.*;
import org.junit.experimental.categories.Category;

public class QueryConditionTranslatorTest {

    @Test()
    @Category({UnitTest.class})
    public void ToQueryJsonStringTest() throws Exception {
        String conditions = "["
            + "{ \"Key\": \"Tags.Building\", \"Operator\": \"EQ\", \"Value\": 43 },"
            + "{ \"Key\": \"Properties.Reported.Type\", \"Operator\": \"EQ\", \"Value\": \"Chiller\" },"
            + "{ \"Key\": \"Properties.Desired.IsSimulated\", \"Operator\": \"EQ\", \"Value\": true }"
            + "]";
        String query = QueryConditionTranslator.ToQueryString(conditions);
        String expected = "Tags.Building = 43 and Properties.Reported.Type = 'Chiller' and Properties.Desired.IsSimulated = true";
        Assert.assertEquals(query, expected);
    }

    @Test()
    @Category({UnitTest.class})
    public void ToQueryRawStringTest() throws Exception {
        String conditions = "Tags.Building = 43 and Properties.Reported.Type = \"Chiller\"";
        String query = QueryConditionTranslator.ToQueryString(conditions);
        String expected = "Tags.Building = 43 and Properties.Reported.Type = 'Chiller'";
        Assert.assertEquals(query, expected);
    }

    @Test()
    @Category({UnitTest.class})
    public void ToQueryEmptyStringTest() throws Exception {
        Assert.assertEquals(QueryConditionTranslator.ToQueryString(""), "");
        Assert.assertEquals(QueryConditionTranslator.ToQueryString("\"\""), "''");
        Assert.assertEquals(QueryConditionTranslator.ToQueryString("[]"), "");
        Assert.assertEquals(QueryConditionTranslator.ToQueryString("[  ]"), "");
    }
}
