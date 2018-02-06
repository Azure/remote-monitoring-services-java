// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.helpers;

import com.google.gson.internal.LinkedTreeMap;
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
                put("key1", "value1");
                put("key2", "value2");
            }
        };
        Set<Pair> pairs = HashMapHelper.mapToSet(table);

        for (Pair p : pairs) {
            Assert.assertTrue(table.get(p.getKey()) == p.getValue());
        }
    }

    @Test(timeout = 1000)
    @Category({UnitTest.class})
    public void MapToHashSetTest() {
        Assert.assertTrue(HashMapHelper.mapToHashSet("", null).size() == 0);

        LinkedTreeMap linkedTreeMap = new LinkedTreeMap();
        linkedTreeMap.put("mapLevel3", "string value");
        HashMap table = new HashMap<String, Object>() {
            {
                put("aString", "string value");
                put("aBool", true);
                put("aNumber", 12.3);
                put("map1Level1", new HashMap<String, String>() {{
                    put("aString", "string value");
                }});
                put("map2Level1", new HashMap<String, Object>() {{
                    put("map2Level2-1", new HashMap<String, Object>() {
                        {
                            put("aString", "string value");
                        }
                    });
                    put("map2Level2-2", linkedTreeMap);
                }});
            }
        };

        HashSet<String> set = HashMapHelper.mapToHashSet("Tags", table);

        List<String> expectedList = Arrays.asList(
            "Tags.aString",
            "Tags.aBool",
            "Tags.aNumber",
            "Tags.map1Level1.aString",
            "Tags.map2Level1.map2Level2-1.aString",
            "Tags.map2Level1.map2Level2-2.mapLevel3"
        );
        Assert.assertTrue(set.containsAll(expectedList));
        Assert.assertTrue(expectedList.containsAll(set));

        HashSet<String> setWithoutPrefix = HashMapHelper.mapToHashSet("", table);

        List<String> expectedListWithoutPrefix = Arrays.asList(
                "aString",
                "aBool",
                "aNumber",
                "map1Level1.aString",
                "map2Level1.map2Level2-1.aString",
                "map2Level1.map2Level2-2.mapLevel3"
        );
        Assert.assertTrue(setWithoutPrefix.containsAll(expectedListWithoutPrefix));
        Assert.assertTrue(expectedListWithoutPrefix.containsAll(setWithoutPrefix));
    }
}
