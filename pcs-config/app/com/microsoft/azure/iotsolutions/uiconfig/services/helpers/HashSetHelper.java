// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.helpers;

import java.util.HashMap;
import java.util.HashSet;

public class HashSetHelper {

    public static void preparePropNames(HashSet<String> set, Object obj, String prefix) {
        if (obj instanceof String || obj instanceof Boolean || obj instanceof Number) {
            set.add(prefix);
            return;
        }
        HashMap<String, ? extends Object> map = (HashMap<String, ? extends Object>) obj;
        map.entrySet().stream().forEach(m -> {
            preparePropNames(set, m.getValue(), String.format("%s.%s", prefix, m.getKey()));
        });
    }
}
