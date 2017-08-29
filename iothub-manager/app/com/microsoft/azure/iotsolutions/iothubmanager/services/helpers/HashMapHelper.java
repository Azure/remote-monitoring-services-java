// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.services.helpers;

import com.microsoft.azure.sdk.iot.service.devicetwin.Pair;

import java.util.HashMap;
import java.util.*;

/**
 * This class is used to convert Set<Pair> and HashMap<String, Object>
 * IoTHub SDK intentionally return Set<Pair> which is not easy to consume.
 * So we expose these two private methods of DeviceTwinDevice to be public.
 */
public class HashMapHelper {

    public static HashMap<String, Object> setToHashMap(Set<Pair> set)
    {
        HashMap<String, Object> map = new HashMap<>();

        if (set != null)
        {
            for (Pair p : set)
            {
                map.put(p.getKey(), p.getValue());
            }
        }

        return map;
    }

    public static Set<Pair> mapToSet(Map<String, Object> map)
    {
        Set<Pair> setPair = new HashSet<>();

        if (map != null)
        {
            for (Map.Entry<String, Object> setEntry : map.entrySet())
            {
                setPair.add(new Pair(setEntry.getKey(), setEntry.getValue()));
            }
        }

        return setPair;

    }
}
