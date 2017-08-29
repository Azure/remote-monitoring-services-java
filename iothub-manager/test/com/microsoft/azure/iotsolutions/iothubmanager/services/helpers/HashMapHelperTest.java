// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.helpers;

import com.microsoft.azure.sdk.iot.service.devicetwin.Pair;

import helpers.UnitTest;
import org.junit.*;
import org.junit.experimental.categories.Category;

import java.util.*;

public class HashMapHelperTest {

    @Before
    public void setUp() {
        // something before every test
    }

    @After
    public void tearDown() {
        // something after every test
    }

    @Test(timeout = 1000)
    @Category({UnitTest.class})
    public void SetToHashMapTest() {
        Assert.assertTrue(HashMapHelper.setToHashMap(null).size() == 0);

        Set<Pair> pairs = new HashSet();
        pairs.add(new Pair("key1", "value1"));
        pairs.add(new Pair("key2", "value2"));

        HashMap map = HashMapHelper.setToHashMap(pairs);
        Assert.assertTrue(map.get("key1") == "value1");
        Assert.assertTrue(map.get("key2") == "value2");
    }

    @Test(timeout = 1000)
    @Category({UnitTest.class})
    public void MapToSetTest() {
        Assert.assertTrue(HashMapHelper.mapToSet(null).size() == 0);

        HashMap table = new HashMap<String, String>() {
            {
                put("ke1", "value1");
                put("key2", "value2");
            }
        };
        Set<Pair> pairs = HashMapHelper.mapToSet(table);

        for (Pair p : pairs) {
            Assert.assertTrue(table.get(p.getKey()) == p.getValue());
        }
    }
}
